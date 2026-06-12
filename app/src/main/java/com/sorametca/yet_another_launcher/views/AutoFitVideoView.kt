package com.sorametca.yet_another_launcher.views

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class AutoFitVideoView(context: Context, attrs: AttributeSet) : VideoView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(0, widthMeasureSpec)
        val height = getDefaultSize(0, heightMeasureSpec)

        if (width > 0 && height > 0) {
            val videoRatio = width.toFloat() / height.toFloat()
            val screenRatio = width.toFloat() / height.toFloat()

            val finalWidth: Int
            val finalHeight: Int

            if (videoRatio > screenRatio) {
                finalHeight = height
                finalWidth = (height * videoRatio).toInt()
            } else {
                finalWidth = width
                finalHeight = (width / videoRatio).toInt()
            }
            setMeasuredDimension(finalWidth, finalHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}