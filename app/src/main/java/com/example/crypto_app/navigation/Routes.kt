package com.example.crypto_app.navigation

import com.example.crypto_app.data.model.CoinResponse
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

object Routes {
    const val HOME = "home"
    const val PORTFOLIO = "portfolio"
    const val SETTINGS = "settings"
    private const val DETAIL = "detail"

    fun detailRoute(coin: CoinResponse): String {
        val coinJson = Gson().toJson(coin)
        val encodedJson = URLEncoder.encode(coinJson, "UTF-8")
        return "$DETAIL/$encodedJson"
    }

    fun detailRoutePattern(): String = "$DETAIL/{coinJson}"

    fun parseCoinFromArgument(encodedCoinJson: String?): CoinResponse? {
        return try {
            val coinJson = encodedCoinJson?.let { URLDecoder.decode(it, "UTF-8") }
            coinJson?.let { Gson().fromJson(it, CoinResponse::class.java) }
        } catch (e: Exception) {
            null
        }
    }
}
