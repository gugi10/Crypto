package com.example.cryptotracker.data.model

import com.squareup.moshi.Json

data class MarketChart(
    @field:Json(name = "prices")
    val prices: List<PricePoint>,

    @field:Json(name = "market_caps")
    val marketCaps: List<PricePoint>,

    @field:Json(name = "total_volumes")
    val totalVolumes: List<PricePoint>
)
