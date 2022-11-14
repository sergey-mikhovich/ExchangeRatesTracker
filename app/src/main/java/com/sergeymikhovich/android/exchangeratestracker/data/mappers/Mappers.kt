package com.sergeymikhovich.android.exchangeratestracker.data.mappers

import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.network.ExchangeRatesResponse

class Mappers {

    fun toFavoriteExchangeRateEntity(
        exchangeRateEntity: ExchangeRateEntity
    ): FavoriteExchangeRateEntity {
        with(exchangeRateEntity) {
            return FavoriteExchangeRateEntity(
                rateName = rateName,
                rateQuantity = rateQuantity,
                baseName = baseName,
                date = date,
                timestamp = timestamp
            )
        }
    }

    fun toExchangeRateEntities(
        response: ExchangeRatesResponse
    ): List<ExchangeRateEntity> {
        return if (response.rates.isNotEmpty()) {
            response.rates.map { entry ->
                with(response) {
                    ExchangeRateEntity(
                        rateName = entry.key,
                        rateQuantity = entry.value,
                        baseName = base,
                        date = date,
                        timestamp = timestamp
                    )
                }
            }
        } else {
            emptyList()
        }
    }
}