package com.sergeymikhovich.android.exchangeratestracker.ui

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.sergeymikhovich.android.exchangeratestracker.data.ExchangeRatesRepository
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExchangeRatesRepository
) : ViewModel() {

    var selectedBase = ""
    var selectedFavoriteBase = ""

    var selectedSorting: Sorting = Sorting.NoSorting
    var selectedFavoriteSorting: Sorting = Sorting.NoSorting

    var listStateParcel: Parcelable? = null
    var listStateFavoriteParcel: Parcelable? = null
}