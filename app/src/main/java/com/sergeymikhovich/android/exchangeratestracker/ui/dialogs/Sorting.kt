package com.sergeymikhovich.android.exchangeratestracker.ui.dialogs

sealed class Sorting {
    object NoSorting: Sorting()
    object ByAlphabeticAsc: Sorting()
    object ByAlphabeticDesc: Sorting()
    object ByValueAsc: Sorting()
    object ByValueDesc: Sorting()
}