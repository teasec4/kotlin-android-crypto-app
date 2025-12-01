package com.example.crypto_app.data.repository

import com.example.crypto_app.data.api.SupabaseAuthService
import com.example.crypto_app.data.model.UserResponse

class AuthRepositoryImpl(private val supabaseAuthService: SupabaseAuthService) : AuthRepository {
    override suspend fun loginUser(email: String, password: String): Result<UserResponse> {
        return try {
            val user = supabaseAuthService.loginUser(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<UserResponse> {
        return try {
            val user = supabaseAuthService.registerUser(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        return try {
            supabaseAuthService.logOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<UserResponse?> {
        return try {
            val user = supabaseAuthService.currentUser()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
