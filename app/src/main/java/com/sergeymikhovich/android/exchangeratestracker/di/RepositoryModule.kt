package com.sergeymikhovich.android.exchangeratestracker.di

import com.sergeymikhovich.android.exchangeratestracker.data.ExchangeRatesRepository
import com.sergeymikhovich.android.exchangeratestracker.data.ExchangeRatesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(impl: ExchangeRatesRepositoryImpl): ExchangeRatesRepository
}