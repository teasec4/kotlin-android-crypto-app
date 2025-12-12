package com.example.crypto_app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface Route: NavKey{
    @Serializable
    data object LoginRoute: Route, NavKey

    @Serializable
    data object RegisterRoute: Route, NavKey

    @Serializable
    data object HomeRoute: Route, NavKey

    @Serializable
    data object PortfolioRoute: Route, NavKey

    @Serializable
    data object SettingsRoute: Route, NavKey
}



