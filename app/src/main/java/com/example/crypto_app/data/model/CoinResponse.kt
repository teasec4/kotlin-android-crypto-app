package com.example.crypto_app.data.model

import com.google.gson.annotations.SerializedName

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
    fun getSafePrice(): Double = currentPrice ?: 0.0
    fun getSafePriceChange24h(): Double = priceChange24h ?: 0.0
}
