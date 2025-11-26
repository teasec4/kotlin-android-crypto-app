package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.crypto_app.domain.usecase.GetCoinsUseCase

class HomeViewModelFactory(private val getCoinsUseCase: GetCoinsUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            HomeViewModel(getCoinsUseCase) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
