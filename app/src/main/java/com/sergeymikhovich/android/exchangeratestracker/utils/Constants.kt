package com.sergeymikhovich.android.exchangeratestracker.utils

class Constants {

    companion object {
        // ROOM
        const val DATABASE_NAME = "exchange_rates_database"
        const val EXCHANGE_RATES_TABLE = "exchange_rates_table"
        const val FAVORITE_EXCHANGE_RATES_TABLE = "favorite_exchange_rates_table"

        // API
        const val BASE_URL = "https://api.apilayer.com"
        const val API_KEY = "ZgukoYWApBRM1CnEWUmm2Gbjpmzzljps"
        const val HEADER_API_KEY = "apikey"

        //Formats
        const val DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"

    }
}