package com.example.crypto_app.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.crypto_app.di.ServiceLocator
import com.example.crypto_app.ui.viewmodel.DetailUiState
import com.example.crypto_app.ui.viewmodel.DetailViewModel

@Composable
fun DetailViewCoin(coinId: String, modifier: Modifier = Modifier) {
    val homeViewModel = ServiceLocator.createHomeViewModel()
    val homeUiState = homeViewModel.uiState.collectAsState().value
    
    val detailViewModel = when (homeUiState) {
        is com.example.crypto_app.ui.viewmodel.HomeUiState.Success -> {
            val coinUIList = homeUiState.coins.map { 
                com.example.crypto_app.data.model.CoinUI.fromCoinResponse(it) 
            }
            DetailViewModel(coinId, coinUIList)
        }
        else -> null
    }
    
    if (detailViewModel == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    val detailUiState = detailViewModel.uiState.collectAsState().value

    when (detailUiState) {
        DetailUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is DetailUiState.Success -> {
            val coin = detailUiState.coin
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = coin.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${coin.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coin.symbol.uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        is DetailUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${detailUiState.message}")
            }
        }
    }
}
