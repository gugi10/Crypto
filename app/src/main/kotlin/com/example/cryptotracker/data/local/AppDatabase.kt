package com.example.cryptotracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cryptotracker.data.model.Coin

@Database(entities = [Coin::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
}
