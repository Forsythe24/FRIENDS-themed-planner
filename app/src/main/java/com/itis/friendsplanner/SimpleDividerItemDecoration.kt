package com.itis.friendsplanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class SimpleDividerItemDecoration(
    private val itemOffset: Int,
    private val context: Context
    ) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        with (outRect) {
            top = itemOffset
            bottom = itemOffset
        }
    }

    private val dividerList = listOf(
        R.drawable.red_circle_divider,
        R.drawable.blue_circle_divider,
        R.drawable.yellow_circle_divider,
        R.drawable.red_circle_divider,
        R.drawable.yellow_circle_divider,
        R.drawable.blue_circle_divider)


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = 515
        val right = parent.width - left
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin + 20

            val mDivider: Drawable = ContextCompat.getDrawable(context, dividerList[i % 6])!!

            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}
