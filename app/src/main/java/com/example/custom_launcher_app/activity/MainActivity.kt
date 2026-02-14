package com.example.custom_launcher_app.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import com.example.custom_launcher_app.OnSwipeListener
import com.example.custom_launcher_app.R

class MainActivity : OnSwipeListener() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onLongPress(e: MotionEvent) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onSwipeToTop() {
        val intent = Intent(this, AppDrawerActivity::class.java)
        startActivity(intent)
    }
}