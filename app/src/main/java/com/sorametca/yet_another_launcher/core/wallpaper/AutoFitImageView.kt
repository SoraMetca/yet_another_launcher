package com.sorametca.yet_another_launcher.core.wallpaper

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AutoFitImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    init {
        scaleType = ScaleType.FIT_XY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}