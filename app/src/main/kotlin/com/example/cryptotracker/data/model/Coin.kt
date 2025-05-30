package com.example.cryptotracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "coins") // Keep table name simple
data class Coin(
    @PrimaryKey
    @field:Json(name = "id")
    val id: String,

    @field:Json(name = "symbol")
    val symbol: String,

    @field:Json(name = "name")
    val name: String,

    @field:Json(name = "image")
    val image: String?, // URL, can be nullable

    @field:Json(name = "current_price")
    val currentPrice: Double?,

    @field:Json(name = "market_cap")
    val marketCap: Long?,

    @field:Json(name = "market_cap_rank")
    val marketCapRank: Int?,

    @field:Json(name = "total_volume")
    val totalVolume: Double?,

    @field:Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double?,

    @field:Json(name = "circulating_supply")
    val circulatingSupply: Double?,

    @field:Json(name = "total_supply")
    val totalSupply: Double?,

    @field:Json(name = "max_supply")
    val maxSupply: Double?,

    @field:Json(name = "ath") // All-time high
    val ath: Double?,

    @field:Json(name = "ath_change_percentage")
    val athChangePercentage: Double?,

    @field:Json(name = "ath_date")
    val athDate: String?,

    @field:Json(name = "atl") // All-time low
    val atl: Double?,

    @field:Json(name = "atl_change_percentage")
    val atlChangePercentage: Double?,

    @field:Json(name = "atl_date")
    val atlDate: String?,

    @field:Json(name = "last_updated")
    val lastUpdated: String?
)
