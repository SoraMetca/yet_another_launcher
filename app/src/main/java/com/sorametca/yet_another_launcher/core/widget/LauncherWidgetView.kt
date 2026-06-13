package com.sorametca.yet_another_launcher.core.widget

import android.appwidget.AppWidgetHostView
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.sorametca.yet_another_launcher.activity.MainActivity

class LauncherWidgetView(context: Context) : AppWidgetHostView(context) {
    private var downX = 0f
    private var downY = 0f
    private val longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()

    private val longPressRunnable = Runnable {
        // Only trigger if the launcher is completely idle
        if (!MainActivity.isAppDrawerOpen && !MainActivity.isDraggingInProgress) {
            performLongClick()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (MainActivity.isAppDrawerOpen || MainActivity.isDraggingInProgress) {
            removeCallbacks(longPressRunnable)
            return false
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                postDelayed(longPressRunnable, longPressTimeout)
            }
            MotionEvent.ACTION_MOVE -> {
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                if (Math.abs(ev.x - downX) > slop || Math.abs(ev.y - downY) > slop) {
                    removeCallbacks(longPressRunnable)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(longPressRunnable)
            }
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (MainActivity.isAppDrawerOpen || MainActivity.isDraggingInProgress) {
            removeCallbacks(longPressRunnable)
            return super.onTouchEvent(ev)
        }
        return super.onTouchEvent(ev)
    }
}