package com.example.crypto_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа от CoinGecko API.
 * Nullable значения обрабатываются с дефолтными значениями для безопасности.
 */
data class CoinResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double? = 0.0,
    @SerializedName("price_change_percentage_24h")
    val priceChange24h: Double? = 0.0,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int? = null
) {
    /**
     * Безопасное получение цены с дефолтным значением.
     */
    fun getSafePrice(): Double = currentPrice ?: 0.0

    /**
     * Безопасное получение изменения цены за 24 часа.
     */
    fun getSafePriceChange24h(): Double = priceChange24h ?: 0.0
}
