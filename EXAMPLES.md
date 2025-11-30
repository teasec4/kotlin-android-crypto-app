# 📖 Примеры кода - CryptoApp

Практические примеры для добавления новых функций в CryptoApp.

## 1. Создание нового ViewModel

### Пример: SearchViewModel

```kotlin
// ui/viewmodel/SearchViewModel.kt

package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data object Loading : SearchUiState()
    data class Success(val coins: List<CoinResponse>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val getCoinsUseCase: GetCoinsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        loadAllCoins()
    }
    
    private fun loadAllCoins() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            getCoinsUseCase()
                .onSuccess { coins -> 
                    _uiState.value = SearchUiState.Success(coins)
                }
                .onFailure { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getFilteredCoins(): List<CoinResponse> {
        val state = _uiState.value
        if (state !is SearchUiState.Success) return emptyList()
        
        val query = _searchQuery.value.lowercase()
        return state.coins.filter { coin ->
            coin.name.lowercase().contains(query) ||
            coin.symbol.lowercase().contains(query) ||
            coin.id.lowercase().contains(query)
        }
    }
}
```

### Использование в Composable

```kotlin
// ui/screens/SearchScreen.kt

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val appContainer = LocalAppContainer.current
    
    val viewModel: SearchViewModel = viewModel {
        SearchViewModel(appContainer.getCoinsUseCase)
    }
    
    val uiState = viewModel.uiState.collectAsState().value
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val filteredCoins = remember(uiState, searchQuery) {
        viewModel.getFilteredCoins()
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )
        
        // Results
        when (uiState) {
            SearchUiState.Loading -> LoadingUI()
            is SearchUiState.Success -> {
                LazyColumn {
                    items(filteredCoins) { coin ->
                        CoinTile(coin = coin, onCoinClick = {})
                    }
                }
            }
            is SearchUiState.Error -> ErrorUI((uiState as SearchUiState.Error).message)
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Search coins...") },
        singleLine = true
    )
}
```

## 2. Добавление новой Dependency в AppContainer

### Пример: Добавить CoinDetailsService

```kotlin
// di/AppContainer.kt

class AppContainer(private val context: Context) {
    // ... existing code ...
    
    // Новый сервис для детальной информации о монетах
    val coinDetailsService: CoinDetailsService by lazy {
        retrofit.create(CoinDetailsService::class.java)
    }
    
    // Новый репозиторий для деталей
    val coinDetailsRepository: CoinDetailsRepository by lazy {
        CoinDetailsRepositoryImpl(coinDetailsService)
    }
    
    // Новый use case
    val getCoinDetailsUseCase: GetCoinDetailsUseCase by lazy {
        GetCoinDetailsUseCase(coinDetailsRepository)
    }
}
```

### Использование в ViewModel

```kotlin
// ui/viewmodel/CoinDetailViewModel.kt

class CoinDetailViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val coinId: String
) : ViewModel() {
    private val _coinDetails = MutableStateFlow<CoinDetail?>(null)
    val coinDetails: StateFlow<CoinDetail?> = _coinDetails.asStateFlow()
    
    init {
        loadCoinDetails()
    }
    
    private fun loadCoinDetails() {
        viewModelScope.launch {
            getCoinDetailsUseCase(coinId)
                .onSuccess { details ->
                    _coinDetails.value = details
                }
                .onFailure { error ->
                    // Handle error
                }
        }
    }
}
```

### Использование в экране

```kotlin
@Composable
fun DetailScreen(coinId: String) {
    val appContainer = LocalAppContainer.current
    
    val viewModel: CoinDetailViewModel = viewModel {
        CoinDetailViewModel(appContainer.getCoinDetailsUseCase, coinId)
    }
    
    val coinDetails = viewModel.coinDetails.collectAsState().value
    
    coinDetails?.let { details ->
        DetailUI(details)
    } ?: run {
        LoadingUI()
    }
}
```

## 3. Добавление кэширования

### Пример: CoinCacheManager

```kotlin
// data/cache/CoinCacheManager.kt

package com.example.crypto_app.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.crypto_app.data.model.CoinResponse
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class CoinCacheManager(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) {
    companion object {
        private val COINS_CACHE_KEY = stringPreferencesKey("coins_cache")
        private val CACHE_TIMESTAMP_KEY = longPreferencesKey("cache_timestamp")
        private const val CACHE_DURATION_MS = 5 * 60 * 1000 // 5 минут
    }
    
    suspend fun getCachedCoins(): List<CoinResponse>? {
        return try {
            dataStore.data.map { preferences ->
                val json = preferences[COINS_CACHE_KEY] ?: return@map null
                // Десериализация из JSON
                Json.decodeFromString<List<CoinResponse>>(json)
            }.collect { return it }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun cacheCoins(coins: List<CoinResponse>) {
        dataStore.edit { preferences ->
            val json = Json.encodeToString(coins)
            preferences[COINS_CACHE_KEY] = json
            preferences[CACHE_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }
    
    suspend fun isCacheValid(): Boolean {
        return try {
            dataStore.data.map { preferences ->
                val timestamp = preferences[CACHE_TIMESTAMP_KEY] ?: return@map false
                val age = System.currentTimeMillis() - timestamp
                age < CACHE_DURATION_MS
            }.collect { return it }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun clearCache() {
        dataStore.edit { preferences ->
            preferences.remove(COINS_CACHE_KEY)
            preferences.remove(CACHE_TIMESTAMP_KEY)
        }
    }
}
```

### Интеграция в Repository

```kotlin
// data/repository/CoinRepositoryImpl.kt

class CoinRepositoryImpl(
    private val coinGeckoService: CoinGeckoService,
    private val cacheManager: CoinCacheManager
) : CoinRepository {
    
    override suspend fun getCoins(): Result<List<CoinResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val coins = coinGeckoService.getCoins()
                if (coins.isEmpty()) {
                    Result.failure(Exception("Empty response from API"))
                } else {
                    cacheManager.cacheCoins(coins)
                    Result.success(coins)
                }
            } catch (e: IOException) {
                // Если ошибка сети - вернуть кэш
                val cached = cacheManager.getCachedCoins()
                if (cached != null && cacheManager.isCacheValid()) {
                    Result.success(cached)
                } else {
                    Result.failure(Exception("Network error and no cache", e))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to load coins: ${e.message}", e))
            }
        }
    }
}
```

## 4. Добавление Retry логики

### Пример: RetryHelper

```kotlin
// data/network/RetryHelper.kt

package com.example.crypto_app.data.network

import kotlinx.coroutines.delay

suspend inline fun <T> retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 100,
    maxDelayMs: Long = 5000,
    block: suspend () -> Result<T>
): Result<T> {
    var delay = initialDelayMs
    var lastException: Exception? = null
    
    repeat(maxRetries) { attempt ->
        val result = block()
        if (result.isSuccess) return result
        
        result.exceptionOrNull()?.let { lastException = it }
        
        if (attempt < maxRetries - 1) {
            delay(delay)
            delay = (delay * 2).coerceAtMost(maxDelayMs)
        }
    }
    
    return Result.failure(
        lastException ?: Exception("All retries failed")
    )
}
```

### Использование в Repository

```kotlin
override suspend fun getCoins(): Result<List<CoinResponse>> {
    return retryWithExponentialBackoff(maxRetries = 3) {
        try {
            val coins = coinGeckoService.getCoins()
            if (coins.isEmpty()) {
                Result.failure(Exception("Empty response"))
            } else {
                Result.success(coins)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 5. Добавление Unit тестов

### Пример: HomeViewModelTest

```kotlin
// ui/viewmodel/HomeViewModelTest.kt

package com.example.crypto_app.ui.viewmodel

import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeViewModelTest {
    
    private val mockUseCase = mock<GetCoinsUseCase>()
    private lateinit var viewModel: HomeViewModel
    
    @Before
    fun setup() {
        viewModel = HomeViewModel(mockUseCase)
    }
    
    @Test
    fun testLoadCoinsSuccess() = runTest {
        // Arrange
        val testCoins = listOf(
            CoinResponse(
                id = "bitcoin",
                symbol = "BTC",
                name = "Bitcoin",
                image = "url",
                currentPrice = 50000.0,
                priceChange24h = 5.0,
                marketCapRank = 1
            ),
            CoinResponse(
                id = "ethereum",
                symbol = "ETH",
                name = "Ethereum",
                image = "url",
                currentPrice = 3000.0,
                priceChange24h = 2.0,
                marketCapRank = 2
            )
        )
        
        whenever(mockUseCase()).thenReturn(Result.success(testCoins))
        
        // Act
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertTrue(uiState is HomeUiState.Success)
        assertEquals(2, (uiState as HomeUiState.Success).coins.size)
    }
    
    @Test
    fun testLoadCoinsError() = runTest {
        // Arrange
        val testError = Exception("Network error")
        whenever(mockUseCase()).thenReturn(Result.failure(testError))
        
        // Act
        advanceUntilIdle()
        
        // Assert
        val uiState = viewModel.uiState.value
        assertTrue(uiState is HomeUiState.Error)
    }
    
    @Test
    fun testRefresh() = runTest {
        // Arrange
        val testCoins = listOf(
            CoinResponse("bitcoin", "BTC", "Bitcoin", "url")
        )
        whenever(mockUseCase()).thenReturn(Result.success(testCoins))
        
        // Act
        viewModel.refresh()
        advanceUntilIdle()
        
        // Assert
        assertTrue(viewModel.uiState.value is HomeUiState.Success)
    }
}
```

## 6. Добавление новой зависимости в build.gradle

```kotlin
// app/build.gradle.kts

dependencies {
    // ... existing ...
    
    // Для логирования
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Для кэширования (если используется Room)
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    
    // Для Paging (если нужна подгрузка списков)
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.paging:paging-compose:3.2.0")
    
    // Для тестирования
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-core:5.0.0")
}
```

## 7. Логирование с Timber

### Инициализация

```kotlin
// В Application или MainActivity

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
```

### Использование в коде

```kotlin
// В любом классе
class CoinRepositoryImpl(...) : CoinRepository {
    override suspend fun getCoins(): Result<List<CoinResponse>> {
        try {
            Timber.d("Loading coins from API...")
            val coins = coinGeckoService.getCoins()
            Timber.d("Loaded ${coins.size} coins")
            return Result.success(coins)
        } catch (e: Exception) {
            Timber.e(e, "Error loading coins")
            return Result.failure(e)
        }
    }
}
```

## Резюме

Эти примеры показывают как:
- ✅ Создавать новые ViewModels
- ✅ Добавлять зависимости в AppContainer
- ✅ Реализовать кэширование
- ✅ Добавить retry логику
- ✅ Писать Unit тесты
- ✅ Добавлять логирование

Копируйте эти паттерны для новых функций!

