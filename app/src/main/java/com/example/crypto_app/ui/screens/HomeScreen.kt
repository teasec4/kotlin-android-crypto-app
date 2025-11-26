package com.example.crypto_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.di.ServiceLocator
import com.example.crypto_app.navigation.Routes
import com.example.crypto_app.ui.component.CoinTile
import com.example.crypto_app.ui.viewmodel.HomeUiState
import com.example.crypto_app.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController?, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = ServiceLocator.createHomeViewModel()
    
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        HomeUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth()
            ) {
                items(uiState.coins) { coin ->
                    CoinTile(
                        coin = coin,
                        onCoinClick = { selectedCoin ->
                            navController?.navigate(Routes.detailRoute(selectedCoin))
                        }
                    )
                }
            }
        }
        is HomeUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${uiState.message}")
            }
        }
    }
}
