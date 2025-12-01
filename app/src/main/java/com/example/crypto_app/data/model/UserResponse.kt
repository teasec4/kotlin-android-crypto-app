package com.example.crypto_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String = "",
    val email: String = "",
)