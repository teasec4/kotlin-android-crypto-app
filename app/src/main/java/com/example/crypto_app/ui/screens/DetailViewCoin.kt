package com.example.crypto_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.crypto_app.data.model.CoinResponse

@Composable
fun DetailViewCoin(coin: CoinResponse, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$${coin.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
