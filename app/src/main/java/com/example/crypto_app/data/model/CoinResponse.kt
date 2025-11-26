package com.example.crypto_app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoinResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double?,
    @SerializedName("price_change_percentage_24h")
    val priceChange24h: Double?,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int?
) : Serializable
