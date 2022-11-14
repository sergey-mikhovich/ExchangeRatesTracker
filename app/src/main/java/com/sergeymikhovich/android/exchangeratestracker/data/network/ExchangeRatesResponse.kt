package com.sergeymikhovich.android.exchangeratestracker.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Int
)