package com.example.crypto_app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.crypto_app.ui.component.CoinTile

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(5) { index ->
            CoinTile(
                symbol = when (index) {
                    0 -> "BTC"
                    1 -> "ETH"
                    2 -> "ADA"
                    3 -> "XRP"
                    else -> "SOL"
                },
                name = when (index) {
                    0 -> "Bitcoin"
                    1 -> "Ethereum"
                    2 -> "Cardano"
                    3 -> "Ripple"
                    else -> "Solana"
                },
                price = when (index) {
                    0 -> "$45,230.50"
                    1 -> "$2,534.80"
                    2 -> "$0.95"
                    3 -> "$2.10"
                    else -> "$145.30"
                },
                change24h = when (index) {
                    0 -> 5.2
                    1 -> -2.1
                    2 -> 3.8
                    3 -> -1.5
                    else -> 8.3
                },
                icon = when (index) {
                    0 -> "₿"
                    1 -> "Ξ"
                    2 -> "₳"
                    3 -> "✕"
                    else -> "◎"
                }
            )
        }
    }
}
