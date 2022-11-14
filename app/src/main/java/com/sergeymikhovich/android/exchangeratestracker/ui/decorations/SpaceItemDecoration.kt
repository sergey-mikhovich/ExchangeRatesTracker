package com.sergeymikhovich.android.exchangeratestracker.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val isReverseRW: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        outRect.top = spacing / 2
        outRect.bottom = spacing / 2
        outRect.left = spacing / 2
        outRect.right = spacing / 2

        if (position < spanCount) {
            if (isReverseRW) {
                outRect.bottom = spacing
            } else {
                outRect.top = spacing
            }
        }

        if (position + spanCount >= itemCount) {
            if (isReverseRW) {
                outRect.top = spacing
            } else {
                outRect.bottom = spacing
            }
        }

        if (position % spanCount == 0) {
            outRect.left = spacing
        }

        if ((position + 1) % spanCount == 0) {
            outRect.right = spacing
        }

    }
}