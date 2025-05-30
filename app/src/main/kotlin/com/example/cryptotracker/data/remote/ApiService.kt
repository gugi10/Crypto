package com.example.cryptotracker.data.remote

import com.example.cryptotracker.data.model.Coin
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("coins/list") // Example endpoint
    suspend fun getCoinList(): Response<List<Coin>>
}
