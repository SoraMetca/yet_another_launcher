package com.example.custom_launcher_app.activity

import android.app.Activity
import android.os.Bundle
import com.example.custom_launcher_app.R

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}