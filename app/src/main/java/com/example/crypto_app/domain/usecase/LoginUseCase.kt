package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.data.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserResponse> {
        return authRepository.loginUser(email, password)
    }
}
