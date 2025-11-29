package com.example.crypto_app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.data.PreferencesManager
import com.example.crypto_app.ui.theme.TextPrimary
import com.example.crypto_app.ui.theme.TextSecondary
import com.example.crypto_app.ui.theme.SurfaceLight
import com.example.crypto_app.ui.theme.SurfaceDarkCard
import com.example.crypto_app.ui.theme.TextPrimaryLight
import com.example.crypto_app.ui.theme.TextSecondaryLight
import com.example.crypto_app.ui.theme.TextPrimaryDark
import com.example.crypto_app.ui.theme.TextSecondaryDark

@Composable
fun CoinTile(
    coin: CoinResponse,
    onCoinClick: (CoinResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val isDarkTheme = preferencesManager.isDarkTheme.collectAsState(initial = false).value
    
    val symbol = coin.symbol.uppercase()
    val price = "$${coin.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}"
    val change24h = coin.priceChange24h ?: 0.0
    val changeColor = if (change24h >= 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
    val changeText = if (change24h >= 0) "+${"%.2f".format(change24h)}%" else "${"%.2f".format(change24h)}%"
    
    val cardColor = if (isDarkTheme) SurfaceDarkCard else SurfaceLight
    val textPrimary = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
    val textSecondary = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
    val iconBgColor = if (isDarkTheme) Color(0xFF3A3A3A) else Color.White.copy(alpha = 0.5f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCoinClick(coin) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = coin.image,
                    contentDescription = "$symbol icon",
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconBgColor, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                
                Column {
                    Text(
                        text = symbol,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary
                    )
                    Text(
                        text = coin.name,
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = changeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
        }
    }
}
