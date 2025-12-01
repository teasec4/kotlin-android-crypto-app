package com.example.crypto_app.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object HomeRoute

@Serializable
object PortfolioRoute

@Serializable
object SettingsRoute

@Serializable
data class DetailRoute(val coinId: String, val coinName: String = "")
