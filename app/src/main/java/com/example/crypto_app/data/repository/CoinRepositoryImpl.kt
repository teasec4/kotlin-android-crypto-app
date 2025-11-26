package com.example.crypto_app.data.repository

import com.example.crypto_app.data.api.CoinGeckoService
import com.example.crypto_app.data.model.CoinResponse

class CoinRepositoryImpl(private val coinGeckoService: CoinGeckoService) : CoinRepository {
    override suspend fun getCoins(): Result<List<CoinResponse>> {
        return try {
            val coins = coinGeckoService.getCoins()
            Result.success(coins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
