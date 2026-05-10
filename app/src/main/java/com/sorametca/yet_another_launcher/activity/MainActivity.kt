package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.PagerObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.adapter.ViewPagerAdapter
import com.sorametca.yet_another_launcher.fragments.AppDrawerFragment
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener


class MainActivity : AppCompatActivity(), OnSwipeInterface {
    private lateinit var viewPager: ViewPager
    private val homeAppList = ArrayList<AppObject>()
    private lateinit var mViewPagerAdapter: ViewPagerAdapter
    private lateinit var swipeListener: OnSwipeListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeHome()

        val main = findViewById<View>(R.id.main)
        main.isClickable = true
        main.isFocusable = true

        swipeListener = object : OnSwipeListener(this, this) {}

        main.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> {
                    val app = event.localState as AppObject
                    homeAppList.add(app)
                    mViewPagerAdapter.notifyGridChanged()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> true
                else -> false
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipeListener.onTouch(null, ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun initializeHome() {
        val pagerAppList = ArrayList<PagerObject>()
        val appList = ArrayList<AppObject>()
        pagerAppList.add(PagerObject(homeAppList))
        pagerAppList.add(PagerObject(appList))
        viewPager = findViewById(R.id.ViewPager)
        mViewPagerAdapter = ViewPagerAdapter(this, pagerAppList)
        viewPager.setAdapter(mViewPagerAdapter)
    }

    override fun onLongPress(e: MotionEvent) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onDoubleTap() {
        val intent = Intent(this, HomeScreenActivity::class.java)
        startActivity(intent)
    }

    override fun onSwipeToTop() {
        if (supportFragmentManager.findFragmentByTag("app_drawer") != null) return

        val fragment = AppDrawerFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_out,
                R.anim.slide_in_up,
                R.anim.slide_in_out
            )
            .replace(R.id.main, fragment, "app_drawer")
            .addToBackStack("app_drawer")
            .commit()
    }
    fun itemPress(app: AppObject) {
        val launchAppIntent =
            applicationContext.packageManager.getLaunchIntentForPackage(app.packageName)
        if (launchAppIntent != null) {
            applicationContext.startActivity(launchAppIntent)
        }
    }
    fun collapseFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }
}