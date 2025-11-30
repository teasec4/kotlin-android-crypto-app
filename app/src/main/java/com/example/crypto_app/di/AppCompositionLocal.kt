package com.example.crypto_app.di

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal для доступа к AppContainer из Compose компонентов.
 * Позволяет избежать передачи зависимостей через параметры в каждый composable.
 */
val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
