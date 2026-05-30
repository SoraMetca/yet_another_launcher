package com.sorametca.yet_another_launcher.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.sorametca.yet_another_launcher.GestureAction
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsValidator
import com.sorametca.yet_another_launcher.adapter.ViewPagerAdapter
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.fragments.AppDrawerFragment
import com.sorametca.yet_another_launcher.fragments.HomeScreenSettingsFragment
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.model.PagerObject
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener


class MainActivity : AppCompatActivity(), OnSwipeInterface {

    // class variables
    private val homeAppList = ArrayList<AppObject>()
    private lateinit var validator: SettingsValidator

    // layout variables
    private lateinit var viewPager: ViewPager
    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    // helpers
    private lateinit var swipeListener: OnSwipeListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeHome()
        validator = SettingsValidator(this.dataStore, lifecycleScope)
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
    }

    fun openHomeScreenSettings(){
        if (supportFragmentManager.findFragmentByTag("home_screen") != null) return
        HomeScreenSettingsFragment().show(supportFragmentManager, "home_screen")
    }
    override fun onDoubleTap() {
        if(validator.gestures["doubleTap"] == GestureAction.openHomeScreenSettings.ordinal) {
           openHomeScreenSettings()
        }else if (validator.gestures["doubleTap"] == GestureAction.openAppList.ordinal){
            openAppList()
        }
    }
    fun openAppList(){
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
    override fun onSwipeToTop() {
        if(validator.gestures["swipeToTop"] == GestureAction.openAppList.ordinal) {
            openAppList()
        }else if(validator.gestures["swipeToTop"] == GestureAction.openHomeScreenSettings.ordinal)
            openHomeScreenSettings()
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