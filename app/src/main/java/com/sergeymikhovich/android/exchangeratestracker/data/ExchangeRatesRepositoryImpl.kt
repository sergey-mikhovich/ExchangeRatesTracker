package com.sergeymikhovich.android.exchangeratestracker.data

import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRatesDao
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRatesDao
import com.sergeymikhovich.android.exchangeratestracker.data.network.ExchangeRatesApi
import com.sergeymikhovich.android.exchangeratestracker.data.network.ExchangeRatesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class ExchangeRatesRepositoryImpl @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val favoriteExchangeRatesDao: FavoriteExchangeRatesDao,
    private val exchangeRatesApi: ExchangeRatesApi,
) : ExchangeRatesRepository {

    override fun getCachedExchangeRates(): Flow<List<ExchangeRateEntity>> {
        return exchangeRatesDao.getCachedExchangeRates()
    }

    override suspend fun updateExchangeRate(exchangeRate: ExchangeRateEntity) {
        exchangeRatesDao.updateExchangeRate(exchangeRate)
    }

    override suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity) {
        exchangeRatesDao.insertExchangeRate(exchangeRate)
    }

    override suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>) {
        exchangeRatesDao.insertExchangeRates(exchangeRates)
    }

    override suspend fun replaceAllExchangeRates(exchangeRates: List<ExchangeRateEntity>) {
        exchangeRatesDao.replaceAllExchangeRates(exchangeRates)
    }


    override fun getFavoriteCachedExchangeRates(): Flow<List<FavoriteExchangeRateEntity>> {
        return favoriteExchangeRatesDao.getFavoriteCachedExchangeRates()
    }

    override suspend fun updateFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity) {
        favoriteExchangeRatesDao.updateFavoriteExchangeRate(exchangeRate)
    }

    override suspend fun insertFavoriteExchangeRate(favoriteExchangeRate: FavoriteExchangeRateEntity) {
        favoriteExchangeRatesDao.insertFavoriteExchangeRate(favoriteExchangeRate)
    }

    override suspend fun insertFavoriteExchangeRates(favoriteExchangeRates: List<FavoriteExchangeRateEntity>) {
        favoriteExchangeRatesDao.insertFavoriteExchangeRates(favoriteExchangeRates)
    }

    override suspend fun deleteFavoriteExchangeRate(exchangeRate: FavoriteExchangeRateEntity) {
        favoriteExchangeRatesDao.deleteFavoriteExchangeRate(exchangeRate)
    }


    override suspend fun getRemoteExchangeRates(base: String): Response<ExchangeRatesResponse> {
        return exchangeRatesApi.getRemoteExchangeRates(base)
    }
}