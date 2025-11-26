package com.example.crypto_app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CoinUI(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    val image: String = "",
    val currentPrice: Double? = null,
    val priceChange24h: Double? = null,
    val marketCapRank: Int? = null
) {
    companion object {
        fun fromCoinResponse(coin: CoinResponse): CoinUI {
            return CoinUI(
                id = coin.id,
                symbol = coin.symbol,
                name = coin.name,
                image = coin.image,
                currentPrice = coin.currentPrice,
                priceChange24h = coin.priceChange24h,
                marketCapRank = coin.marketCapRank
            )
        }
    }
}
