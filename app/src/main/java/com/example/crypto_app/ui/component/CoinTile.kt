package com.example.crypto_app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.crypto_app.data.model.CoinResponse

@Composable
fun CoinTile(
    coin: CoinResponse,
    onCoinClick: (CoinResponse) -> Unit
) {
    val symbol = coin.symbol.uppercase()
    val price = "$${coin.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}"
    val change24h = coin.priceChange24h ?: 0.0
    val changeColor = if (change24h >= 0) Color(0xFF00C853) else Color(0xFFD32F2F)
    val changeText = if (change24h >= 0) "+${"%.2f".format(change24h)}%" else "${"%.2f".format(change24h)}%"

    ListItem(
        headlineContent = { 
            Text(
                text = symbol,
                fontWeight = FontWeight.Bold
            ) 
        },
        supportingContent = { 
            Text(
                text = coin.name,
                fontSize = 12.sp,
                color = Color.Gray
            ) 
        },
        leadingContent = {
            AsyncImage(
                model = coin.image,
                contentDescription = "$symbol icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = changeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
        },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable {
                onCoinClick(coin)
            }
    )
}
