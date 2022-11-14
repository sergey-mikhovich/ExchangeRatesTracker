package com.sergeymikhovich.android.exchangeratestracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

const val DATABASE_VERSION = 1

@Database(
    entities = [ExchangeRateEntity::class, FavoriteExchangeRateEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class ExchangeRatesDatabase : RoomDatabase() {

    abstract fun exchangeRatesDao(): ExchangeRatesDao

    abstract fun favoriteExchangeRatesDao(): FavoriteExchangeRatesDao
}