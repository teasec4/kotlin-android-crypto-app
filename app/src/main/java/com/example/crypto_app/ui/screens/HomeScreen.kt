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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import coil.compose.AsyncImage
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.di.LocalAppContainer
import com.example.crypto_app.ui.component.CoinTile
import com.example.crypto_app.ui.viewmodel.HomeUiState
import com.example.crypto_app.ui.viewmodel.HomeViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.example.crypto_app.ui.theme.Primary
import com.example.crypto_app.ui.theme.Secondary
import com.example.crypto_app.ui.theme.TextSecondary
import com.example.crypto_app.ui.theme.SurfaceDark
import com.example.crypto_app.ui.theme.SurfaceLight
import com.example.crypto_app.ui.theme.TextPrimaryDark
import com.example.crypto_app.ui.theme.TextPrimaryLight
import com.example.crypto_app.ui.theme.TextSecondaryDark


@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (String, String) -> Unit = { _, _ -> }
) {
    val appContainer = LocalAppContainer.current
    val uiState = viewModel.uiState.collectAsState().value
    val selectedCoin = remember { mutableStateOf<CoinResponse?>(null) }
    val text = remember { mutableStateOf("") }

    val isDarkTheme = appContainer.preferencesManager.isDarkTheme.collectAsState(initial = false).value

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
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {
                    items(
                        uiState.coins,
                        key = { it.id }
                    ) { coin ->
                        CoinTile(
                            coin = coin,
                            onCoinClick = { clickedCoin ->
                                selectedCoin.value = clickedCoin
                            },
                            modifier = Modifier.padding(vertical = 6.dp)
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
        val inputAmount = text.value.toDoubleOrNull() ?: 0.0
        val dollarValue = inputAmount * selectedCoin.value!!.getSafePrice()
        val sheetBgColor = if (isDarkTheme) SurfaceDark else Color.White
        val sheetTextPrimary = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
        val sheetTextSecondary = if (isDarkTheme) TextSecondaryDark else Color.Gray
        val textFieldBgColor = if (isDarkTheme) Color(0xFF2A2A2A) else Color.White

        ModalBottomSheet(
            onDismissRequest = { selectedCoin.value = null; text.value = "" },
            scrimColor = Color.Black.copy(alpha = 0.32f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = sheetBgColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Coin info header - simple and clean
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = selectedCoin.value?.image,
                        contentDescription = "${selectedCoin.value?.symbol} icon",
                        modifier = Modifier.size(48.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier.padding(start = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = selectedCoin.value?.name ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = sheetTextPrimary
                        )

                        Text(
                            text = "${selectedCoin.value?.symbol?.uppercase()} • $${selectedCoin.value?.currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                            fontSize = 13.sp,
                            color = sheetTextSecondary
                        )
                    }
                }

                // Input field with conversion rate
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    value = text.value,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            text.value = newValue
                        }
                    },
                    label = {
                        if (text.value.isEmpty()) {
                            Text("Enter amount")
                        } else {
                            Text("= $${String.format("%.2f", dollarValue)}")
                        }
                    },
                    suffix = {
                        Text(
                            text = selectedCoin.value?.symbol?.uppercase() ?: "",
                            fontSize = 14.sp,
                            color = sheetTextSecondary
                        )
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = sheetTextPrimary),
                    shape = RoundedCornerShape(12.dp)
                )

                TextButton(
                    onClick = { selectedCoin.value = null; text.value = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary, shape = RoundedCornerShape(12.dp))
                ) {
                    Text(
                        "Add to Portfolio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}