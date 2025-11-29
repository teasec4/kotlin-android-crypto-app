package com.example.crypto_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.crypto_app.data.PreferencesManager
import com.example.crypto_app.di.ServiceLocator
import com.example.crypto_app.ui.theme.BackgroundDark
import com.example.crypto_app.ui.theme.BackgroundLight
import com.example.crypto_app.ui.theme.TextPrimaryDark
import com.example.crypto_app.ui.theme.TextPrimaryLight
import com.example.crypto_app.ui.viewmodel.HomeUiState

@Composable
fun DetailViewCoin(coinId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val isDarkTheme = preferencesManager.isDarkTheme.collectAsState(initial = false).value
    val backgroundColor = if (isDarkTheme) BackgroundDark else BackgroundLight
    val textColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight

    val homeViewModel = ServiceLocator.createHomeViewModel()
    val homeUiState = homeViewModel.uiState.collectAsState().value

    when (homeUiState) {
        is HomeUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            val coin = homeUiState.coins
                .map { com.example.crypto_app.data.model.CoinUI.fromCoinResponse(it) }
                .find { it.id == coinId }

            if (coin != null) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = coin.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "$${coin.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                }
            } else {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Coin not found", color = textColor)
                }
            }
        }
        is HomeUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${homeUiState.message}", color = textColor)
            }
        }
    }
}
