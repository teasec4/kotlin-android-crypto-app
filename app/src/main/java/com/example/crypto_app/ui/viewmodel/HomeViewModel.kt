package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.domain.usecase.GetCoinsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val coins: List<CoinResponse>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel для главного экрана со списком крипто-валют.
 * Управляет состоянием загрузки, успеха и ошибок.
 */
class HomeViewModel(private val getCoinsUseCase: GetCoinsUseCase) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getCoinsUseCase()
                .onSuccess { coins ->
                    _uiState.value = HomeUiState.Success(coins)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun refresh() {
        loadCoins()
    }
}
