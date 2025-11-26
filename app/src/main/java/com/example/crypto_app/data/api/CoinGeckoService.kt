package com.example.crypto_app.data.api

import com.example.crypto_app.data.model.CoinResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinResponse>
}
