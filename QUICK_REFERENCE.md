# Quick Reference - CryptoApp

## 🚀 Быстрый старт

### Запуск приложения
```bash
./gradlew installDebug
# или в Android Studio нажать Run
```

### Структура проекта
```
app/src/main/java/com/example/crypto_app/
├── MainActivity.kt              # Главная активность, DI инициализация
├── data/
│   ├── api/
│   │   └── CoinGeckoService.kt  # Retrofit интерфейс
│   ├── model/
│   │   └── CoinResponse.kt       # Модель данных от API
│   ├── repository/
│   │   ├── CoinRepository.kt     # Интерфейс репозитория
│   │   └── CoinRepositoryImpl.kt  # Реализация с обработкой ошибок
│   └── PreferencesManager.kt     # Управление настройками (тема)
├── domain/
│   └── usecase/
│       └── GetCoinsUseCase.kt    # Бизнес логика
├── di/
│   ├── AppContainer.kt           # DI контейнер (ГЛАВНЫЙ)
│   └── AppCompositionLocal.kt    # CompositionLocal для доступа
├── navigation/                   # Маршруты навигации
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt         # Список монет
│   │   ├── PortfolioScreen.kt    # Портфель (мок)
│   │   ├── SettingsScreen.kt     # Настройки (тема)
│   │   └── DetailViewCoin.kt     # Детали монеты
│   ├── component/
│   │   └── CoinTile.kt           # Компонент плитки монеты
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt      # ViewModel для списка монет
│   │   └── SettingsViewModel.kt  # ViewModel для настроек
│   └── theme/
│       └── Theme.kt              # Тема приложения
```

## 🔑 Основные классы

### AppContainer - DI контейнер
```kotlin
// Создание (одна на приложение)
val appContainer = AppContainer(context)

// Получение зависимостей
appContainer.preferencesManager      // Settings
appContainer.coinRepository          // Данные
appContainer.getCoinsUseCase         // Бизнес логика
```

### LocalAppContainer - CompositionLocal
```kotlin
// В MainActivity.kt обернуть контент в:
CompositionLocalProvider(AppCompositionLocal provides appContainer) {
    // всё содержимое
}

// В любом composable получить:
val appContainer = LocalAppContainer.current
```

### SettingsViewModel - Управление темой
```kotlin
val viewModel: SettingsViewModel = viewModel {
    SettingsViewModel(appContainer.preferencesManager)
}

val isDarkTheme = viewModel.isDarkTheme.collectAsState().value
viewModel.setDarkTheme(true)  // Установить тему
```

### HomeViewModel - Список монет
```kotlin
val viewModel: HomeViewModel = viewModel {
    HomeViewModel(appContainer.getCoinsUseCase)
}

val uiState = viewModel.uiState.collectAsState().value
when (uiState) {
    HomeUiState.Loading -> { /* показать спиннер */ }
    is HomeUiState.Success -> { /* показать данные */ }
    is HomeUiState.Error -> { /* показать ошибку */ }
}

viewModel.refresh()  // Перезагрузить
```

## 🎯 Типичные операции

### Добавить новую зависимость в DI
```kotlin
// В AppContainer.kt
val newService: MyService by lazy {
    MyService(otherDependency)
}
```

### Создать новый ViewModel
```kotlin
class MyViewModel(
    private val useCase: MyUseCase,
    private val prefs: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow<MyState>(MyState.Loading)
    val state: StateFlow<MyState> = _state.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            try {
                val data = useCase.invoke()
                _state.value = MyState.Success(data)
            } catch (e: Exception) {
                _state.value = MyState.Error(e.message ?: "Unknown")
            }
        }
    }
}
```

### Использовать в Composable
```kotlin
@Composable
fun MyScreen() {
    val appContainer = LocalAppContainer.current
    val viewModel: MyViewModel = viewModel {
        MyViewModel(appContainer.myUseCase, appContainer.preferencesManager)
    }
    
    val state = viewModel.state.collectAsState().value
    
    when (state) {
        is MyState.Loading -> { }
        is MyState.Success -> { }
        is MyState.Error -> { }
    }
}
```

## ⚠️ Ошибки и решения

### Ошибка: "AppContainer not provided"
**Причина:** CompositionLocal не инициализирован
**Решение:**
```kotlin
// Убедитесь, что в MainActivity это сделано:
CompositionLocalProvider(AppCompositionLocal provides appContainer) {
    // Всё содержимое
}
```

### Ошибка: "Cannot find symbol: ServiceLocator"
**Причина:** Старый код ещё использует удалённый ServiceLocator
**Решение:**
```kotlin
// Было:
val viewModel = ServiceLocator.createHomeViewModel()

// Стало:
val appContainer = LocalAppContainer.current
val viewModel = viewModel { HomeViewModel(appContainer.getCoinsUseCase) }
```

### Ошибка: "TimeoutException" при загрузке монет
**Причина:** Долгая загрузка данных или нет интернета
**Решение:** Добавить timeout в OkHttpClient (см. NEXT_STEPS.md)

### Ошибка: "EmptyCoinsException"
**Причина:** API вернул пустой список
**Решение:** Это обрабатывается, должна показаться ошибка "Empty response from API"

## 📊 StateFlow vs Flow

| | Flow | StateFlow |
|---|------|-----------|
| **Тип** | холодный потокс | горячий потовс |
| **Начальное значение** | нет | есть |
| **Подписчики** | получают от точки подписки | получают текущее значение сразу |
| **Отписка** | отписка = остановка | отписка = продолжение |
| **Использование** | события, запросы | состояние UI |

## 🔄 Жизненные циклы

### Activity → ViewModel → UI
```
onCreate() → AppContainer создан
    ↓
setContent { } → Composable инициализируется
    ↓
CompositionLocal предоставляет AppContainer
    ↓
composable получает AppContainer
    ↓
viewModel { } создаёт ViewModel и запускает init {}
    ↓
collectAsState() слушает StateFlow
    ↓
UI перерисовывается при изменении
    ↓
onDestroy() → ViewModel.onCleared() → корутины отменяются
```

## 💾 Сохранение состояния

### Тема сохраняется автоматически
```kotlin
// SettingsScreen изменяет:
viewModel.setDarkTheme(true)

// Это вызывает:
preferencesManager.setDarkTheme(true)

// Что сохраняет в DataStore (файл системы)
// И при перезагрузке приложения - загружается обратно
```

## 🧪 Тестирование компонентов

### Тест ViewModel
```kotlin
@Test
fun test() = runTest {
    val mockUseCase = mock<GetCoinsUseCase>()
    whenever(mockUseCase()).thenReturn(Result.success(listOf(...)))
    
    val viewModel = HomeViewModel(mockUseCase)
    advanceUntilIdle()  // дождаться завершения корутин
    
    assert(viewModel.uiState.value is HomeUiState.Success)
}
```

## 🚨 Правила безопасности

❌ НЕЛЬЗЯ:
- Создавать новые AppContainer в Composable
- Использовать ServiceLocator (удалён)
- Создавать PreferencesManager вручную
- Хранить в Singleton глобальное состояние UI

✅ МОЖНО:
- Получать AppContainer через LocalAppContainer.current
- Использовать viewModel { } для ViewModel
- Передавать ViewModel как параметр между экранами
- Использовать StateFlow для состояния

## 📞 Дебаг

### Логирование состояния
```kotlin
val state = viewModel.state.collectAsState().value
println("Current state: $state")  // Добавить Timber позже
```

### Проверка данных в DataStore
```kotlin
// Данные хранятся в:
// /data/data/com.example.crypto_app/files/datastore/settings.preferences_pb
// Можно посмотреть через Android Studio Device File Explorer
```

### Сетевые запросы
```kotlin
// Можно добавить логирование в CoinRepositoryImpl
// или использовать Network Inspector в Android Studio
```

## 🎓 Полезные паттерны

### Управление множественными состояниями
```kotlin
sealed class UIState {
    object Loading : UIState()
    data class Success(val data: List<Item>) : UIState()
    data class Error(val message: String) : UIState()
}

// Использование
when (state) {
    is UIState.Loading -> LoadingUI()
    is UIState.Success -> SuccessUI(state.data)
    is UIState.Error -> ErrorUI(state.message)
}
```

### Комбинирование потоков (StateFlow)
```kotlin
val combined = combine(flow1, flow2) { val1, val2 ->
    // логика
    val1 + val2
}.stateIn(viewModelScope, SharingStarted.Lazily, initial)
```

