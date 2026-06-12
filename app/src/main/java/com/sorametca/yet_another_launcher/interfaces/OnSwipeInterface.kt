package com.sorametca.yet_another_launcher.interfaces





import android.view.MotionEvent





// necessary, as activity allows for only one class in superlist. i can't import OnSwipeListener, but interface i can


interface OnSwipeInterface {


    open fun onSwipeToTop() {}


    open fun onSwipeToBottom() {}





    open fun onSwipeLeft(){}


    open fun onSwipeRight(){}


    open fun onLongPress(e: MotionEvent) {}


    open fun onDoubleTap() {}


}