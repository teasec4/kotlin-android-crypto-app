package com.example.crypto_app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.crypto_app.ui.viewmodel.AuthViewModel
import com.example.crypto_app.ui.viewmodel.HomeViewModel
import com.example.crypto_app.ui.viewmodel.SettingsViewModel

class ViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(appContainer.getCoinsUseCase) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                appContainer.loginUseCase,
                appContainer.registerUseCase,
                appContainer.logoutUseCase,
                appContainer.getCurrentUserUseCase
            ) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(appContainer.preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
