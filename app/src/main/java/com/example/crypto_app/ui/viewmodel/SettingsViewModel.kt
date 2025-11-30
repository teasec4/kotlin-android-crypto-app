package com.example.crypto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto_app.data.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel для управления настройками приложения.
 * Преобразует Flow из PreferencesManager в StateFlow для удобного использования в UI.
 */
class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * StateFlow с состоянием тёмной темы.
     * Автоматически переходит в SharingStarted.Lazily при отписке всех слушателей.
     */
    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    /**
     * Установить состояние тёмной темы.
     */
    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkTheme(isDark)
        }
    }
}
