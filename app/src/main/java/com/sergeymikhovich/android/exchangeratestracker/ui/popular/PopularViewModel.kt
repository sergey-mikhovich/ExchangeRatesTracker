package com.sergeymikhovich.android.exchangeratestracker.ui.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeymikhovich.android.exchangeratestracker.data.ExchangeRatesRepository
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.mappers.Mappers
import com.sergeymikhovich.android.exchangeratestracker.data.network.ExchangeRatesResponse
import com.sergeymikhovich.android.exchangeratestracker.data.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val repository: ExchangeRatesRepository,
    private val mappers: Mappers
) : ViewModel() {

    private val _responseState =
        MutableStateFlow<NetworkResult<List<ExchangeRateEntity>>>(NetworkResult.Loading())
    val responseState: StateFlow<NetworkResult<List<ExchangeRateEntity>>> = _responseState

    val cachedExchangeRates: Flow<List<ExchangeRateEntity>> = repository.getCachedExchangeRates()

    private val favoriteCachedExchangeRates: Flow<List<FavoriteExchangeRateEntity>> =
        repository.getFavoriteCachedExchangeRates()

    fun getRemoteExchangeRates(base: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _responseState.value = NetworkResult.Loading()
            try {
                val response = repository.getRemoteExchangeRates(base)
                _responseState.value = handleExchangeRatesResponse(response)
            } catch (e: Exception) {
                _responseState.value = NetworkResult.Error(e.toString())
            }
        }
    }

    private fun handleExchangeRatesResponse(
        response: Response<ExchangeRatesResponse>
    ): NetworkResult<List<ExchangeRateEntity>> {
        return when {
            response.isSuccessful -> {
                val exchangeRatesResponse = response.body()!!
                val exchangeRatesEntities = mappers.toExchangeRateEntities(exchangeRatesResponse)
                onCacheExchangeRatesResponse(exchangeRatesEntities)
                NetworkResult.Success(exchangeRatesEntities)
            }
            else -> return NetworkResult.Error(response.errorBody().toString())
        }
    }

    private fun onCacheExchangeRatesResponse(exchangeRateEntities: List<ExchangeRateEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExchangeRates(exchangeRateEntities)
        }
    }

    fun replaceAllExchangeRates(exchangeRates: List<ExchangeRateEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.replaceAllExchangeRates(exchangeRates)
        }
    }

    fun onFavoriteClick(exchangeRate: ExchangeRateEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteExchangeRateEntity =
                favoriteCachedExchangeRates.first().firstOrNull { favoriteExchangeRate ->
                    favoriteExchangeRate.rateName == exchangeRate.rateName &&
                    favoriteExchangeRate.baseName == exchangeRate.baseName
                }

            if (favoriteExchangeRateEntity != null) {
                if (!exchangeRate.isFavorite) {
                    repository.updateFavoriteExchangeRate(
                        favoriteExchangeRateEntity.copy(
                            rateQuantity = exchangeRate.rateQuantity,
                            date = exchangeRate.date,
                            timestamp = exchangeRate.timestamp
                        )
                    )
                } else {
                    repository.deleteFavoriteExchangeRate(favoriteExchangeRateEntity)
                }
            } else {
                repository.insertFavoriteExchangeRate(mappers.toFavoriteExchangeRateEntity(exchangeRate))
            }

            repository.updateExchangeRate(exchangeRate.copy(isFavorite = !exchangeRate.isFavorite))
        }
    }
}