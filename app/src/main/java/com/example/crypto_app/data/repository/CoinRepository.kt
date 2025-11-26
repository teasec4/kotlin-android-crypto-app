package com.example.crypto_app.data.repository

import com.example.crypto_app.data.model.CoinResponse

interface CoinRepository {
    suspend fun getCoins(): Result<List<CoinResponse>>
}
