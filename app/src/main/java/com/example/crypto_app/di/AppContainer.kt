package com.example.crypto_app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.crypto_app.data.PreferencesManager
import com.example.crypto_app.data.api.CoinGeckoService
import com.example.crypto_app.data.repository.CoinRepository
import com.example.crypto_app.data.repository.CoinRepositoryImpl
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Dependency Injection контейнер приложения.
 * Singleton для всех зависимостей.
 */
class AppContainer(private val context: Context) {
    // ========== DataStore ==========
    private val dataStore: DataStore<Preferences> by lazy {
        context.dataStore
    }

    // ========== Preferences ==========
    val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(dataStore)
    }

    // ========== Network ==========
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val coinGeckoService: CoinGeckoService by lazy {
        retrofit.create(CoinGeckoService::class.java)
    }

    // ========== Repository ==========
    val coinRepository: CoinRepository by lazy {
        CoinRepositoryImpl(coinGeckoService)
    }

    // ========== Use Cases ==========
    val getCoinsUseCase: GetCoinsUseCase by lazy {
        GetCoinsUseCase(coinRepository)
    }
}
