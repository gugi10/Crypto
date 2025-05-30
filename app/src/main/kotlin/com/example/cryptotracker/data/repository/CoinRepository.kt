package com.example.cryptotracker.data.repository

import com.example.cryptotracker.data.local.CoinDao
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.remote.CoinGeckoApiService // Updated import
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val apiService: CoinGeckoApiService, // Updated type
    private val coinDao: CoinDao
) {
    fun getAllCoins(): Flow<List<Coin>> {
        return coinDao.getAllCoins()
    }

    suspend fun refreshCoins(vsCurrency: String = "usd") { // Added parameter
        try {
            // Call the new getCoinMarkets method
            val response = apiService.getCoinMarkets(vsCurrency = vsCurrency)
            if (response.isSuccessful) {
                response.body()?.let { coins ->
                    // Filter out coins with null id before inserting, though id is non-null in model
                    // This is more of a safeguard if API could potentially send null ids
                    // Since Coin.id is non-nullable @PrimaryKey, filtering is not strictly needed
                    // but doesn't hurt if API could theoretically send bad data.
                    coinDao.insertCoins(coins)
                }
            } else {
                // Handle API error (e.g., log, expose error state)
                // For now, just printing to logcat for placeholder
                println("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            // Handle network or other exceptions
            // For now, just printing to logcat for placeholder
            println("Network/Exception: ${e.message}")
        }
    }
    // Placeholder for fetching market chart data
    // suspend fun getMarketChartData(coinId: String, vsCurrency: String, days: String) { ... }
}
