package com.example.crypto_app.data.repository

import com.example.crypto_app.data.api.CoinGeckoService
import com.example.crypto_app.data.model.CoinResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeoutException
class CoinRepositoryImpl(private val coinGeckoService: CoinGeckoService) : CoinRepository {
    override suspend fun getCoins(): Result<List<CoinResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val coins = coinGeckoService.getCoins()
                // Проверяем, что список не пустой
                if (coins.isEmpty()) {
                    Result.failure(Exception("Empty response from API"))
                } else {
                    Result.success(coins)
                }
            } catch (e: TimeoutException) {
                // Таймаут сети
                Result.failure(Exception("Network timeout: ${e.message}", e))
            } catch (e: IOException) {
                // Ошибка сети (нет интернета, etc)
                Result.failure(Exception("Network error: ${e.message}", e))
            } catch (e: Exception) {
                // Другие ошибки (парсинг JSON, etc)
                Result.failure(Exception("Failed to load coins: ${e.message}", e))
            }
        }
    }
}
