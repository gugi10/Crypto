package com.example.cryptotracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins")
data class Coin(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String
)
