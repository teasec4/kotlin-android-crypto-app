package com.example.crypto_app.domain.usecase

import com.example.crypto_app.data.model.CoinResponse
import com.example.crypto_app.data.repository.CoinRepository

class GetCoinsUseCase(private val coinRepository: CoinRepository) {
    suspend operator fun invoke(): Result<List<CoinResponse>> {
        return coinRepository.getCoins()
    }
}
