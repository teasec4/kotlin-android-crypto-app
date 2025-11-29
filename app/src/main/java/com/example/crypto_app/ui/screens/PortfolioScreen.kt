package com.example.crypto_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.ui.component.CoinTile

@Composable
fun PortfolioScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6200EE),
                                Color(0xFF3700B3)
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Total Balance",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "$348.50",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Portfolio Value",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "+12.5%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Text(
                            text = "●●●● ●●●● ●●●● 5678",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(MockCoins) { coin ->
                CoinTile(
                    coin = coin,
                    onCoinClick = {}
                )
            }
        }

    }
}

var MockCoins = listOf<CoinResponse>(
    CoinResponse(
        id = "bitcoin",
        symbol = "BTC",
        name = "Bitcoin",
        image = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
        currentPrice = 45000.0,
        priceChange24h = 2.5,
        marketCapRank = 1
    ),
    CoinResponse(
        id = "ethereum",
        symbol = "ETH",
        name = "Ethereum",
        image = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
        currentPrice = 2500.0,
        priceChange24h = -1.2,
        marketCapRank = 2
    ),
    CoinResponse(
        id = "cardano",
        symbol = "ADA",
        name = "Cardano",
        image = "https://assets.coingecko.com/coins/images/975/large/cardano.png",
        currentPrice = 0.95,
        priceChange24h = 3.1,
        marketCapRank = 4
    )
)