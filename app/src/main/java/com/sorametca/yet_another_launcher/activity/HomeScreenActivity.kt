package com.sorametca.yet_another_launcher.activity

import android.app.Activity
import android.os.Bundle
import com.sorametca.yet_another_launcher.R

class HomeScreenActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen_settings_activity)
    }
}