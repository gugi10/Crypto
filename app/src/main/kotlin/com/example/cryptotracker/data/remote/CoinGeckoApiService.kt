package com.example.cryptotracker.data.remote

import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChart
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApiService {

    @GET("coins/markets")
    suspend fun getCoinMarkets(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order: String = "market_cap_desc", // e.g., market_cap_desc, gecko_desc, volume_desc
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("price_change_percentage") priceChangePercentage: String = "24h", // e.g., 1h, 24h, 7d, 14d, 30d, 200d, 1y
        @Query("locale") locale: String = "en"
    ): Response<List<Coin>> // Assuming Coin.kt is now the detailed model

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String, // Coin ID, e.g., "bitcoin"
        @Query("vs_currency") vsCurrency: String,
        @Query("days") days: String, // e.g., 1, 7, 14, 30, 90, 180, 365, "max"
        @Query("interval") interval: String? = null, // "daily" for data interval of 1 day. Null for auto.
        @Query("precision") precision: String? = "full" // "full" or number of decimal places
    ): Response<MarketChart>
}
