package com.example.crypto_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.di.ServiceLocator
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
    val selectedCoin = remember { mutableStateOf<CoinResponse?>(null) }
    val text = remember { mutableStateOf("") }

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
                            onCoinClick = { clickedCoin ->
                                selectedCoin.value = clickedCoin
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

    // Bottom Sheet
    if (selectedCoin.value != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedCoin.value = null },
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Coin Image
                    AsyncImage(
                        model = selectedCoin.value?.image,
                        contentDescription = "${selectedCoin.value?.symbol} icon",
                        modifier = Modifier.size(100.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Coin Name
                        Text(
                            text = selectedCoin.value?.name ?: "",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        // Coin Symbol
                        Text(
                            text = selectedCoin.value?.symbol?.uppercase() ?: "",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }


                    // Price
                    Text(
                        text = "$${selectedCoin.value?.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)),
                    value = text.value,
                    label = {Text("Tap a value")},
                    onValueChange = { text.value = it }
                )

                TextButton({}) {
                    Text("Enter")
                }
            }
        }
    }
}