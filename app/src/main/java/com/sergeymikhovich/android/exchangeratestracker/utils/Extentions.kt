package com.sergeymikhovich.android.exchangeratestracker.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(@StringRes messageRes: Int, anchorView: View, length: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, messageRes, length)
    snack.anchorView = anchorView
    snack.show()
}