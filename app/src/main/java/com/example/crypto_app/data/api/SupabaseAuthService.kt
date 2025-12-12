package com.example.crypto_app.data.api

import io.github.jan.supabase.auth.Auth
import com.example.crypto_app.data.model.UserResponse
import io.github.jan.supabase.auth.providers.builtin.Email

class SupabaseAuthService(private val auth: Auth) {
    suspend fun loginUser(email: String, password: String): UserResponse {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = auth.currentUserOrNull()
                ?: throw Exception("Login failed: user not found after successful sign-in")
            UserResponse(
                id = user.id,
                email = user.email ?: "",
            )
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    suspend fun registerUser(email: String, password: String): UserResponse {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = auth.currentUserOrNull()
                ?: throw Exception("Registration failed: user not found after successful sign-up. Email confirmation might be required.")
            UserResponse(
                id = user.id,
                email = user.email ?: "",
            )
        } catch (e: Exception) {
            throw Exception("Registration failed: ${e.message}")
        }
    }

    suspend fun logOut() {
        auth.signOut()
    }

    suspend fun currentUser(): UserResponse? {
        return try {
            // need delay to response from supabase
            kotlinx.coroutines.delay(500)
            val user = auth.currentUserOrNull()
            if (user != null) {
                UserResponse(
                    id = user.id,
                    email = user.email ?: "",
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
