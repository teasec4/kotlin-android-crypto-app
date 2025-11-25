package com.example.crypto_app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoinTile(
    symbol: String,
    name: String,
    price: String,
    change24h: Double,
    icon: String = "🪙"
) {
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
                text = name,
                fontSize = 12.sp,
                color = Color.Gray
            ) 
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
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
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}
