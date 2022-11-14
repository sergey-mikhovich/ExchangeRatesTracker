package com.sergeymikhovich.android.exchangeratestracker.di

import com.sergeymikhovich.android.exchangeratestracker.data.mappers.Mappers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun provideMappers(): Mappers {
        return Mappers()
    }
}