package com.example.custom_launcher_app.activity

import android.content.Intent
import android.os.Bundle
import com.example.custom_launcher_app.OnSwipeListener
import com.example.custom_launcher_app.R

class AppDrawerActivity : OnSwipeListener() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_drawer)
    }

    override fun onSwipeToBottom() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}