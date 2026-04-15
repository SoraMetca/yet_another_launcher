package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.fragments.AppDrawerFragment
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener


class MainActivity : AppCompatActivity(), OnSwipeInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeDrawer()

        val main = findViewById<View>(R.id.main)
        main.isClickable = true
        main.isFocusable = true

        main.setOnTouchListener(
            object : OnSwipeListener(this, this) {}
        )
    }

    private fun initializeDrawer() {
        val bottomSheet = findViewById<View>(R.id.bottomSheet)
        findViewById<GridView>(R.id.drawerGrid)
        BottomSheetBehavior.from(bottomSheet)
    }

    override fun onLongPress(e: MotionEvent) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onSwipeToTop() {

        val fragment = AppDrawerFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_out,
                R.anim.slide_in_up,
                R.anim.slide_in_out
            )
            .replace(R.id.main, fragment)
            .addToBackStack("app_drawer")
            .commit()
    }
}