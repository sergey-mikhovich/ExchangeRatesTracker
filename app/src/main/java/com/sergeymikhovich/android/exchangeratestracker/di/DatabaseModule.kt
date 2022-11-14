package com.sergeymikhovich.android.exchangeratestracker.di

import android.content.Context
import androidx.room.Room
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRatesDao
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRatesDatabase
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRatesDao
import com.sergeymikhovich.android.exchangeratestracker.utils.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExchangeRatesDatabase {
        return Room.databaseBuilder(
            context,
            ExchangeRatesDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideExchangeRatesDao(database: ExchangeRatesDatabase): ExchangeRatesDao {
        return database.exchangeRatesDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteExchangeRatesDao(database: ExchangeRatesDatabase): FavoriteExchangeRatesDao {
        return database.favoriteExchangeRatesDao()
    }
}