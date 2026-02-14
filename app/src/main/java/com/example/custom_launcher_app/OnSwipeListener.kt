package com.example.custom_launcher_app

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import kotlin.math.abs

abstract class OnSwipeListener : ComponentActivity(), GestureDetector.OnGestureListener {
    private lateinit var gestureDetector: GestureDetector
    val swipeThreshold = 100
    val swipeVelocityThreshold = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetector = GestureDetector(this, this)
    }

    // Override this method to recognize touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    // All the below methods are GestureDetector.OnGestureListener members
    // Except onFling, all must "return false" if Boolean return type
    // and "return" if no return type
    override fun onDown(e: MotionEvent): Boolean {
        return false
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
                        // Left -> Right - TODO
                    } else {
                        // Right -> Left - TODO
                    }
                    return true
                }
            } else {

                // Vertical swipe
                if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    if (diffY > 0) {
                        onSwipeToBottom()
                    } else {
                        onSwipeToTop()
                    }
                    return true
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    open fun onSwipeToTop() {}
    open fun onSwipeToBottom() {}
}