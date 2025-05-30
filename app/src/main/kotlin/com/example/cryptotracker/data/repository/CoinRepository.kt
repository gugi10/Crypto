package com.example.cryptotracker.data.repository

import com.example.cryptotracker.data.local.CoinDao
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val apiService: ApiService,
    private val coinDao: CoinDao
) {
    fun getAllCoins(): Flow<List<Coin>> {
        // Basic implementation: fetch from DB, try to refresh from network
        // More sophisticated logic will be needed here
        return coinDao.getAllCoins()
    }

    suspend fun refreshCoins() {
        try {
            val response = apiService.getCoinList()
            if (response.isSuccessful) {
                response.body()?.let { coinDao.insertCoins(it) }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
