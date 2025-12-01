package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.domain.usecase.GetCurrentUserUseCase
import com.example.crypto_app.domain.usecase.LoginUseCase
import com.example.crypto_app.domain.usecase.LogoutUseCase
import com.example.crypto_app.domain.usecase.RegisterUseCase
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
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiStateAuth = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiStateAuth: StateFlow<AuthUiState> = _uiStateAuth.asStateFlow()

    init {
        getCurrentUser()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiStateAuth.value = AuthUiState.Loading
            loginUseCase(email, password)
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
            registerUseCase(email, password)
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
            logoutUseCase()
                .onSuccess {
                    _uiStateAuth.value = AuthUiState.Success(null)
                }
                .onFailure { error ->
                    _uiStateAuth.value = AuthUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { user ->
                    _uiStateAuth.value = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    _uiStateAuth.value = AuthUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
