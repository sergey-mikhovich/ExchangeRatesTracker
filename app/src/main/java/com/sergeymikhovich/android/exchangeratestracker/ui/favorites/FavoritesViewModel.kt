package com.sergeymikhovich.android.exchangeratestracker.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeymikhovich.android.exchangeratestracker.data.ExchangeRatesRepository
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ExchangeRatesRepository
) : ViewModel() {

    val favoriteCachedExchangeRates: Flow<List<FavoriteExchangeRateEntity>> =
        repository.getFavoriteCachedExchangeRates()

    private val cachedExchangeRates: Flow<List<ExchangeRateEntity>> = repository.getCachedExchangeRates()

    fun onFavoriteClick(favoriteExchangeRate: FavoriteExchangeRateEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteExchangeRate(favoriteExchangeRate)

            cachedExchangeRates.first().forEach { cachedExchangeRate ->
                if (cachedExchangeRate.rateName == favoriteExchangeRate.rateName &&
                     cachedExchangeRate.baseName == favoriteExchangeRate.baseName
                ) {
                    repository.updateExchangeRate(cachedExchangeRate.copy(isFavorite = false))
                }
            }
        }
    }
}