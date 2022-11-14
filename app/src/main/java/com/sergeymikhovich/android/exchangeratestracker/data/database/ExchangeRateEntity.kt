package com.sergeymikhovich.android.exchangeratestracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sergeymikhovich.android.exchangeratestracker.utils.Constants.Companion.EXCHANGE_RATES_TABLE
import java.util.*

@Entity(tableName = EXCHANGE_RATES_TABLE)
data class ExchangeRateEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val rateName: String,
    val rateQuantity: Double,
    val baseName: String,
    val date: String,
    val timestamp: Int,
    val isFavorite: Boolean = false
)
