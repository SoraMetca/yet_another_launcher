package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.os.Bundle
import com.sorametca.yet_another_launcher.OnSwipeListener
import com.sorametca.yet_another_launcher.R

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