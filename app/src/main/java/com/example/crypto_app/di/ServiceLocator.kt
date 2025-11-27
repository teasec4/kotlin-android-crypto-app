package com.example.crypto_app.di

import com.example.crypto_app.data.api.CoinGeckoService
import com.example.crypto_app.data.repository.CoinRepository
import com.example.crypto_app.data.repository.CoinRepositoryImpl
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import com.example.crypto_app.ui.viewmodel.HomeViewModel
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: CoinGeckoService by lazy {
        retrofit.create(CoinGeckoService::class.java)
    }

    private val coinRepository: CoinRepository by lazy {
        CoinRepositoryImpl(apiService)
    }

    private val getCoinsUseCase by lazy {
        GetCoinsUseCase(coinRepository)
    }

    private var homeViewModel: HomeViewModel? = null

    fun createHomeViewModel(): HomeViewModel {
        return homeViewModel ?: HomeViewModel(getCoinsUseCase).also {
            homeViewModel = it
        }
    }
    // long version of it
    //fun createHomeViewModel(): HomeViewModel {
    //    if (homeViewModel != null) {
    //        return homeViewModel!!
    //    }
    //
    //    val newVm = HomeViewModel(getCoinsUseCase)
    //    homeViewModel = newVm
    //    return newVm

}
