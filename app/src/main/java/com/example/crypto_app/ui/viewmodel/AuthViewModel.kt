package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Loading : AuthUiState()
    data class Success(val user: UserResponse?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiStateAuth = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiStateAuth: StateFlow<AuthUiState> = _uiStateAuth.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val result = authUseCase.getCurrentUser()
                result
                    .onSuccess { user ->
                        _uiStateAuth.value = AuthUiState.Success(user)
                    }
                    .onFailure { error ->
                        // Если нет текущего пользователя, показываем экран входа
                        _uiStateAuth.value = AuthUiState.Success(null)
                    }
            } catch (e: Exception) {
                _uiStateAuth.value = AuthUiState.Success(null)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiStateAuth.value = AuthUiState.Loading
            authUseCase.login(email, password)
                .onSuccess { user ->
                    _uiStateAuth.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiStateAuth.value = AuthUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiStateAuth.value = AuthUiState.Loading
            authUseCase.register(email, password)
                .onSuccess { user ->
                    _uiStateAuth.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiStateAuth.value = AuthUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
                .onSuccess {
                    _uiStateAuth.value = AuthUiState.Success(null)
                }
                .onFailure { error ->
                    _uiStateAuth.value = AuthUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
