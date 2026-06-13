package com.sorametca.yet_another_launcher.core.swipes

import android.view.MotionEvent

interface OnSwipeInterface {


    open fun onSwipeToTop() {}


    open fun onSwipeToBottom() {}





    open fun onSwipeLeft(){}


    open fun onSwipeRight(){}


    open fun onLongPress(e: MotionEvent) {}


    open fun onDoubleTap() {}


}