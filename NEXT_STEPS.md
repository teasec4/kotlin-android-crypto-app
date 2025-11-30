# Следующие шаги для CryptoApp

## 🔴 КРИТИЧЕСКИЕ (нужно сделать сейчас)

### 1. Провести сборку проекта
```bash
./gradlew clean build
# или в Android Studio: Build → Clean Project, потом Build → Make Project
```

**Что проверить:**
- [ ] Нет ошибок компиляции
- [ ] Нет ошибок импорта
- [ ] Приложение запускается
- [ ] Загружается список монет
- [ ] Переключение темы работает
- [ ] Все экраны работают

### 2. Удалить неиспользуемый код
```kotlin
// В PortfolioScreen.kt - эти MockCoins нужно удалить позже
// когда добавите реальное управление портфелем
var MockCoins = listOf<CoinResponse>( ... )
```

## 🟡 ВАЖНЫЕ (нужно сделать в следующей очереди)

### 3. Добавить кэширование данных

Создать `CoinCacheManager.kt`:
```kotlin
class CoinCacheManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val COINS_CACHE_KEY = stringPreferencesKey("coins_cache")
        private val CACHE_TIMESTAMP_KEY = longPreferencesKey("cache_timestamp")
        private const val CACHE_DURATION_MS = 5 * 60 * 1000 // 5 минут
    }
    
    suspend fun getCachedCoins(): List<CoinResponse>? {
        // Десериализация из JSON
    }
    
    suspend fun cacheCoins(coins: List<CoinResponse>) {
        // Сериализация в JSON
    }
    
    suspend fun isCacheValid(): Boolean {
        // Проверка времени кэша
    }
}
```

Обновить `CoinRepositoryImpl`:
```kotlin
override suspend fun getCoins(): Result<List<CoinResponse>> {
    return withContext(Dispatchers.IO) {
        try {
            val coins = coinGeckoService.getCoins()
            if (coins.isEmpty()) {
                Result.failure(Exception("Empty response"))
            } else {
                cacheManager.cacheCoins(coins) // сохранить кэш
                Result.success(coins)
            }
        } catch (e: Exception) {
            // Если ошибка - вернуть кэшированные данные
            val cached = cacheManager.getCachedCoins()
            if (cached != null && cacheManager.isCacheValid()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }
}
```

### 4. Добавить retry логику

Создать `RetryHelper.kt`:
```kotlin
suspend inline fun <T> retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 100,
    block: suspend () -> Result<T>
): Result<T> {
    var delay = initialDelayMs
    repeat(maxRetries) {
        val result = block()
        if (result.isSuccess) return result
        if (it < maxRetries - 1) {
            delay(delay)
            delay *= 2
        }
    }
    return block() // последняя попытка
}
```

Использовать в `CoinRepositoryImpl`:
```kotlin
override suspend fun getCoins(): Result<List<CoinResponse>> {
    return retryWithExponentialBackoff { 
        // код загрузки
    }
}
```

### 5. Добавить timeout для сетевых запросов

Обновить `AppContainer.kt`:
```kotlin
private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
}

private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}
```

## 🟢 РЕКОМЕНДУЕМЫЕ (улучшения)

### 6. Добавить logging

Добавить в `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

Создать `LoggingInterceptor.kt`:
```kotlin
class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        Timber.d("→ Запрос: ${request.url}")
        
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime
        
        Timber.d("← Ответ: ${response.code} за ${duration}ms")
        return response
    }
}
```

### 7. Добавить SSL Pinning

Обновить `AppContainer.kt`:
```kotlin
private val certificatePinner: CertificatePinner by lazy {
    CertificatePinner.Builder()
        .add("api.coingecko.com", "sha256/your-cert-hash")
        .build()
}

private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()
}
```

### 8. Добавить Paging для больших списков

Если список монет растёт, добавить:
```gradle
implementation("androidx.paging:paging-runtime-ktx:3.1.1")
implementation("androidx.paging:paging-compose:3.2.0")
```

### 9. Добавить поиск/фильтр монет

Создать `SearchViewModel.kt`:
```kotlin
class SearchViewModel(private val getCoinsUseCase: GetCoinsUseCase) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    
    val filteredCoins: StateFlow<List<CoinResponse>> = 
        combine(getCoinsUseCase(), searchQuery) { coins, query ->
            if (query.isEmpty()) coins
            else coins.filter { 
                it.name.contains(query, ignoreCase = true) ||
                it.symbol.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}
```

### 10. Добавить детальный экран монеты

Обновить `DetailViewCoin`:
```kotlin
@Composable
fun DetailViewCoin(coinId: String, viewModel: CoinDetailViewModel) {
    val state = viewModel.coinDetails.collectAsState().value
    
    when (state) {
        is Loading -> LoadingUI()
        is Success -> DetailUI(state.coin)
        is Error -> ErrorUI(state.message)
    }
}
```

## 📋 Чеклист для выпуска v1.1

- [ ] Кэширование данных работает
- [ ] Retry логика работает
- [ ] Timeout установлен
- [ ] Logging показывает все операции
- [ ] SSL Pinning реализован
- [ ] Поиск по монетам работает
- [ ] Детальный экран монеты работает
- [ ] Управление портфелем имеет БД
- [ ] Все тесты зелёные
- [ ] Приложение протестировано на слабом интернете

## 🧪 Примеры тестов

### Unit тест для ViewModel

```kotlin
@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {
    
    private val mockUseCase = mock<GetCoinsUseCase>()
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        viewModel = HomeViewModel(mockUseCase)
    }
    
    @Test
    fun testLoadCoinsSuccess() = runTest {
        val testCoins = listOf(
            CoinResponse("bitcoin", "BTC", "Bitcoin", "...", 50000.0),
            CoinResponse("ethereum", "ETH", "Ethereum", "...", 3000.0)
        )
        
        whenever(mockUseCase.invoke()).thenReturn(Result.success(testCoins))
        
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        assertEquals(2, (viewModel.uiState.value as HomeUiState.Success).coins.size)
    }
    
    @Test
    fun testLoadCoinsError() = runTest {
        whenever(mockUseCase.invoke()).thenReturn(
            Result.failure(Exception("Network error"))
        )
        
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
    }
}
```

## 📚 Полезные ресурсы

- [CoinGecko API Documentation](https://docs.coingecko.com/reference/coins-markets)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Architecture Components](https://developer.android.com/guide/architecture)
- [Coroutines Best Practices](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)

