package com.sorametca.yet_another_launcher.activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import com.sorametca.yet_another_launcher.OnSwipeListener
import com.sorametca.yet_another_launcher.R
import com.google.android.material.bottomsheet.BottomSheetBehavior


class MainActivity : OnSwipeListener() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeDrawer()
    }

    private fun initializeDrawer() {
        val bottomSheet = findViewById<View>(R.id.bottomSheet)
        val drawerGridView = findViewById<GridView>(R.id.drawerGrid)
        val bottomSheetBehavIor = BottomSheetBehavior.from(bottomSheet)
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