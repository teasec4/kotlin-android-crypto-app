package com.example.crypto_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.crypto_app.di.ServiceLocator
import com.example.crypto_app.navigation.DetailRoute
import com.example.crypto_app.ui.component.CoinTile
import com.example.crypto_app.ui.viewmodel.HomeUiState
import com.example.crypto_app.ui.viewmodel.HomeViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController?, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = ServiceLocator.createHomeViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    val pullState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullState,
        isRefreshing = uiState is HomeUiState.Loading,
        onRefresh = {
            viewModel.refresh()
        }
    ) {
        when (uiState) {
            HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.coins) { coin ->
                        CoinTile(
                            coin = coin,
                            onCoinClick = { selectedCoin ->
                                navController?.navigate(DetailRoute(selectedCoin.id, selectedCoin.name))
                            }
                        )
                    }
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${uiState.message}")
                }
            }
        }
    }
}