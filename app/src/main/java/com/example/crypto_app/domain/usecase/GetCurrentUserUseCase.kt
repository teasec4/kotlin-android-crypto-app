package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.data.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<UserResponse?> {
        return authRepository.getCurrentUser()
    }
}