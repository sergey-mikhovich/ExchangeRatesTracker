package com.sergeymikhovich.android.exchangeratestracker.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesApi {

    @GET("/exchangerates_data/latest")
    suspend fun getRemoteExchangeRates(
        @Query("base") base: String
    ): Response<ExchangeRatesResponse>
}