package com.sergeymikhovich.android.exchangeratestracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteExchangeRatesDao {

    @Query("SELECT * FROM favorite_exchange_rates_table")
    fun getFavoriteCachedExchangeRates(): Flow<List<FavoriteExchangeRateEntity>>

    @Update
    suspend fun updateFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity)

    @Delete
    suspend fun deleteFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity)
}