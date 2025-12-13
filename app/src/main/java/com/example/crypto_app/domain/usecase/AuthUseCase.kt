package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.data.repository.AuthRepository

/**
 * Use case для всех операций аутентификации.
 * Объединяет функции логина, регистрации, выхода и получения текущего пользователя.
 */
class AuthUseCase(private val authRepository: AuthRepository) {
    
    /**
     * Вход в систему
     */
    suspend fun login(email: String, password: String): Result<UserResponse> {
        return authRepository.loginUser(email, password)
    }

    /**
     * Регистрация нового пользователя
     */
    suspend fun register(email: String, password: String): Result<UserResponse> {
        return authRepository.registerUser(email, password)
    }

    /**
     * Выход из системы
     */
    suspend fun logout(): Result<Unit> {
        return authRepository.logoutUser()
    }

    /**
     * Получить текущего пользователя
     */
    suspend fun getCurrentUser(): Result<UserResponse?> {
        return authRepository.getCurrentUser()
    }
}