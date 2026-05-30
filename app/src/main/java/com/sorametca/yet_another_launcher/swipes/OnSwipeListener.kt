package com.sorametca.yet_another_launcher.swipes

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import kotlin.math.abs

abstract class OnSwipeListener(context: Context,
                               private val mInterface: OnSwipeInterface
) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {
    private val gestureDetector = GestureDetector(context, this)
    val swipeThreshold = 100
    val swipeVelocityThreshold = 100

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    // All the below methods are GestureDetector.OnGestureListener members
    // Except onFling, all must "return false" if Boolean return type
    // and "return" if no return type
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
//        if (e1 == null) return false
//
//        val diffY = e2.y - e1.y
//        print(diffY)
//
//        if (diffY > swipeThreshold) {
//            mInterface.onSwipeToBottom()
//            return true
//        }
//
//        if (diffY < -swipeThreshold) {
//            mInterface.onSwipeToTop()
//            return true
//        }

        return false
    }

    // This method is called when a swipe gesture is detected
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x

            if (abs(diffX) > abs(diffY)) {

                // Horizontal swipe
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        mInterface.onSwipeRight()
                    } else {
                        mInterface.onSwipeRight()
                    }
                    return true
                }
            } else {

                 //Vertical swipe
                if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    if (diffY > 0) {
                        mInterface.onSwipeToBottom()
                    } else {
                        mInterface.onSwipeToTop()
                    }
                    return true
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        mInterface.onDoubleTap()
        return true
    }
    override fun onLongPress(e: MotionEvent) {
        mInterface.onLongPress(e)
    }
}