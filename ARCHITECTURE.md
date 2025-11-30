# CryptoApp - Архитектура после рефакторинга

## 🎯 Диаграмма архитектуры

```
┌─────────────────────────────────────────────────────────────┐
│                       MainActivity                          │
│    • Создаёт AppContainer (только один раз)                │
│    • Предоставляет через CompositionLocal                  │
│    • Инициализирует SettingsViewModel                      │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │  AppContainer  │ ◄─── SINGLETON
        │ (DI Container) │
        └────┬───────────┘
             │
             ├─── DataStore
             ├─── PreferencesManager (Singleton) ◄─── одни экземпляр для всего приложения
             │     └── isDarkTheme: Flow<Boolean>
             ├─── Retrofit API
             │     └── CoinGeckoService
             ├─── CoinRepository (Singleton)
             │     └── Обработка ошибок сети, retry, кэш
             └─── GetCoinsUseCase (Singleton)
                  └── Бизнес логика загрузки монет
```

## 🔑 Ключевые концепции

### 1. **AppContainer** - Dependency Injection контейнер
```kotlin
val appContainer = AppContainer(context)
// Все зависимости инициализируются лениво (by lazy)
// Существует один экземпляр на все приложение
```

### 2. **CompositionLocal** - Доступ к AppContainer в Compose
```kotlin
CompositionLocalProvider(AppCompositionLocal provides appContainer) {
    // Теперь любой composable может получить AppContainer
    val appContainer = LocalAppContainer.current
}
```

### 3. **SettingsViewModel** - Единая точка управления темой
```kotlin
val settingsViewModel = viewModel { SettingsViewModel(appContainer.preferencesManager) }
val isDarkTheme = settingsViewModel.isDarkTheme.collectAsState() // StateFlow, не Flow
```

### 4. **HomeViewModel** - Управление состоянием списка монет
```kotlin
val homeViewModel: HomeViewModel = viewModel {
    HomeViewModel(appContainer.getCoinsUseCase)
}
// ViewModel автоматически управляется lifecycle
```

## 📊 State Management

### Global State (Theme)
```
MainActivity
    └── SettingsViewModel
        └── isDarkTheme: StateFlow<Boolean>  ◄─── слушают все экраны
```

### Screen State
```
HomeScreen
    └── HomeViewModel
        └── uiState: StateFlow<HomeUiState>
            ├── Loading
            ├── Success(coins)
            └── Error(message)
```

## 🔄 Data Flow

### Загрузка монет
```
HomeScreen
    ├─ вызывает: homeViewModel.refresh()
    │
    ├─ HomeViewModel
    │  ├─ viewModelScope.launch { getCoinsUseCase() }
    │  │
    │  ├─ GetCoinsUseCase
    │  │  ├─ coinRepository.getCoins()
    │  │  │
    │  │  ├─ CoinRepositoryImpl
    │  │  │  ├─ withContext(Dispatchers.IO)
    │  │  │  ├─ coinGeckoService.getCoins()  [Network Call]
    │  │  │  ├─ Обработка ошибок сети
    │  │  │  └─ Result.success() или Result.failure()
    │  │  │
    │  │  └─ CoinGeckoService (Retrofit)
    │  │     └─ HTTP GET /coins/markets
    │  │
    │  └─ .onSuccess/.onFailure обновляет _uiState
    │
    └─ UI перерисовывается когда uiState изменяется
```

### Изменение темы
```
SettingsScreen
    ├─ пользователь нажимает Toggle Dark Theme
    ├─ settingsViewModel.setDarkTheme(it)
    │
    ├─ SettingsViewModel
    │  ├─ viewModelScope.launch { preferencesManager.setDarkTheme(isDark) }
    │  │
    │  ├─ PreferencesManager
    │  │  ├─ dataStore.edit { preferences ->
    │  │  │   preferences[DARK_THEME_KEY] = isDark
    │  │  │ }
    │  │  └─ DataStore (система)
    │  │
    │  └─ isDarkTheme StateFlow эмитит новое значение
    │
    ├─ MainActivity видит изменение isDarkTheme
    └─ UI перерисовывается с новой темой (CryptoappTheme(darkTheme = isDarkTheme))
```

## 🛠️ Как использовать в новых экранах

### Создать новый экран с доступом к данным

```kotlin
@Composable
fun NewScreen(modifier: Modifier = Modifier) {
    // 1. Получить AppContainer
    val appContainer = LocalAppContainer.current
    
    // 2. Создать ViewModel
    val viewModel: NewViewModel = viewModel {
        NewViewModel(appContainer.getCoinsUseCase) // передать нужные зависимости
    }
    
    // 3. Получить состояние
    val state = viewModel.state.collectAsState().value
    
    // 4. Использовать в UI
    when (state) {
        is Loading -> LoadingUI()
        is Success -> SuccessUI(state.data)
        is Error -> ErrorUI(state.message)
    }
}
```

## ⚠️ Что нельзя делать

### ❌ НЕ создавайте PreferencesManager в composable
```kotlin
// ПЛОХО
val context = LocalContext.current
val preferencesManager = PreferencesManager(context)
```

### ❌ НЕ используйте ServiceLocator
```kotlin
// ПЛОХО (удалён файл!)
val viewModel = ServiceLocator.createHomeViewModel()
```

### ❌ НЕ создавайте новые AppContainer
```kotlin
// ПЛОХО
val appContainer = AppContainer(context)  // каждый раз новый?!
```

## ✅ Правила

### ✅ ВСЕГДА используйте LocalAppContainer для доступа к зависимостям
```kotlin
val appContainer = LocalAppContainer.current
val dependency = appContainer.someService
```

### ✅ ВСЕГДА используйте viewModel { } для создания ViewModels
```kotlin
val viewModel: MyViewModel = viewModel {
    MyViewModel(appContainer.dependency)
}
```

### ✅ ВСЕГДА передавайте ViewModel как параметр если нужен между экранами
```kotlin
// Если нужен в другом экране
composable<MyRoute> {
    MyScreen(sharedViewModel = settingsViewModel)
}
```

## 🧪 Как тестировать

### Тестирование ViewModels

```kotlin
@Test
fun testLoadCoins() = runTest {
    // Создать mock репозитория
    val mockRepository = mock<CoinRepository>()
    whenever(mockRepository.getCoins()).thenReturn(
        Result.success(listOf(testCoin))
    )
    
    // Создать use case
    val useCase = GetCoinsUseCase(mockRepository)
    
    // Создать ViewModel
    val viewModel = HomeViewModel(useCase)
    
    // Проверить состояние
    advanceUntilIdle()
    assertTrue(viewModel.uiState.value is HomeUiState.Success)
}
```

## 📝 Чеклист для добавления нового экрана

- [ ] Создать ViewModel (принимает зависимости в конструкторе)
- [ ] Создать Composable функцию экрана
- [ ] Получить AppContainer: `val appContainer = LocalAppContainer.current`
- [ ] Создать ViewModel: `val viewModel = viewModel { MyViewModel(...) }`
- [ ] Использовать состояние из ViewModel
- [ ] Добавить route в navigation
- [ ] Добавить в NavHost в MainActivity
- [ ] НЕ создавать свои зависимости (использовать из AppContainer)

## 🎓 Полезная литература

- Jetpack Compose: CompositionLocal
- Dependency Injection в Android
- MVVM архитектура
- StateFlow vs Flow
- ViewModels и их lifecycle

