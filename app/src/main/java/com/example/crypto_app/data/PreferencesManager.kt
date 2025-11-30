package com.example.crypto_app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Менеджер для работы с пользовательскими настройками через DataStore.
 * Использует Preferences для безопасного и асинхронного хранения данных.
 */
class PreferencesManager(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    /**
     * Flow с состоянием тёмной темы.
     * Слушатели автоматически уведомляются при изменении.
     */
    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    /**
     * Установить состояние тёмной темы.
     * Подвесная операция - должна вызваться в корутине.
     */
    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }
}
