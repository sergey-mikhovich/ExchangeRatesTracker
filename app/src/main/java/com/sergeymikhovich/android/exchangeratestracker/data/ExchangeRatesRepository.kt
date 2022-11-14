package com.sergeymikhovich.android.exchangeratestracker.data

import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.network.ExchangeRatesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ExchangeRatesRepository {

    fun getCachedExchangeRates(): Flow<List<ExchangeRateEntity>>

    suspend fun updateExchangeRate(exchangeRate: ExchangeRateEntity)

    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    suspend fun replaceAllExchangeRates(exchangeRates: List<ExchangeRateEntity>)


    fun getFavoriteCachedExchangeRates(): Flow<List<FavoriteExchangeRateEntity>>

    suspend fun updateFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity)

    suspend fun insertFavoriteExchangeRate(favoriteExchangeRate: FavoriteExchangeRateEntity)

    suspend fun insertFavoriteExchangeRates(favoriteExchangeRates: List<FavoriteExchangeRateEntity>)

    suspend fun deleteFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity)


    suspend fun getRemoteExchangeRates(base: String): Response<ExchangeRatesResponse>
}