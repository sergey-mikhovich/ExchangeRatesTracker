package com.sergeymikhovich.android.exchangeratestracker.ui.dialogs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Sorting: Parcelable {
    object NoSorting: Sorting()
    object ByAlphabeticAsc: Sorting()
    object ByAlphabeticDesc: Sorting()
    object ByValueAsc: Sorting()
    object ByValueDesc: Sorting()
}