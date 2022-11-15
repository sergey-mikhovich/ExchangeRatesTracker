package com.sergeymikhovich.android.exchangeratestracker.ui

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.Sorting

class MainViewModel: ViewModel() {

    var selectedBase = ""
    var selectedFavoriteBase = ""

    var selectedSorting: Sorting = Sorting.NoSorting
    var selectedFavoriteSorting: Sorting = Sorting.NoSorting

    var recyclerViewStateParcel: Parcelable? = null
    var recyclerViewStateFavoriteParcel: Parcelable? = null
}