package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.data.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserResponse> {
        return authRepository.registerUser(email, password)
    }
}
