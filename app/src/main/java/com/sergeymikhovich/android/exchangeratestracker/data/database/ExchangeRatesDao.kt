package com.sergeymikhovich.android.exchangeratestracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRatesDao {

    @Query("SELECT * FROM exchange_rates_table")
    fun getCachedExchangeRates(): Flow<List<ExchangeRateEntity>>

    @Update
    fun updateExchangeRate(exchangeRate: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates_table")
    suspend fun deleteAllExchangeRates()

    @Transaction
    suspend fun replaceAllExchangeRates(exchangeRates: List<ExchangeRateEntity>) {
        deleteAllExchangeRates()
        insertExchangeRates(exchangeRates)
    }
}