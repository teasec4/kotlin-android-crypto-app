package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logoutUser()
    }
}