package com.example.crypto_app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.crypto_app.data.PreferencesManager
import com.example.crypto_app.data.api.CoinGeckoService
import com.example.crypto_app.data.api.SupabaseAuthService
import com.example.crypto_app.data.repository.AuthRepositoryImpl
import com.example.crypto_app.data.repository.AuthRepository
import com.example.crypto_app.data.repository.CoinRepository
import com.example.crypto_app.data.repository.CoinRepositoryImpl
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import com.example.crypto_app.domain.usecase.GetCurrentUserUseCase
import com.example.crypto_app.domain.usecase.LoginUseCase
import com.example.crypto_app.domain.usecase.LogoutUseCase
import com.example.crypto_app.domain.usecase.RegisterUseCase
import com.example.crypto_app.di.ViewModelFactory
import io.github.jan.supabase.createSupabaseClient

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
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

    // SUPABASE_URL=https://bucuwdkctsufqxgspoqw.supabase.co
    //SUPABASE_ANON_KEY=[REDACTED:jwt-token]
    // ========== Supabase Auth ==========
    private val supabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://bucuwdkctsufqxgspoqw.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ1Y3V3ZGtjdHN1ZnF4Z3Nwb3F3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMyNzc3MjUsImV4cCI6MjA3ODg1MzcyNX0.ynPRHsCLH9bvUIwkFRjRZX5ad6ntQwAdvbkJNMyhHaA"
        ) {
            install(Auth)
        }
    }

    private val supabaseAuth: Auth by lazy {
        supabaseClient.auth
    }

    private val supabaseAuthService: SupabaseAuthService by lazy {
        SupabaseAuthService(supabaseAuth)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(supabaseAuthService)
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

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }

    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }

    val getCurrentUserUseCase: GetCurrentUserUseCase by lazy {
        GetCurrentUserUseCase(authRepository)
    }

    // ========== ViewModel Factory ==========
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}
