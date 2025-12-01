package com.example.crypto_app.data.repository

import com.example.crypto_app.data.model.UserResponse

interface AuthRepository {
    suspend fun loginUser(email: String, password: String): Result<UserResponse>
    suspend fun registerUser(email: String, password: String): Result<UserResponse>
    suspend fun logoutUser(): Result<Unit>
    suspend fun getCurrentUser(): Result<UserResponse?>
}
