package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.crypto_app.data.model.CoinUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val coin: CoinUI) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val coinId: String,
    private val allCoins: List<CoinUI>
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCoin()
    }

    private fun loadCoin() {
        val coin = allCoins.find { it.id == coinId }
        _uiState.value = if (coin != null) {
            DetailUiState.Success(coin)
        } else {
            DetailUiState.Error("Coin not found")
        }
    }
}
