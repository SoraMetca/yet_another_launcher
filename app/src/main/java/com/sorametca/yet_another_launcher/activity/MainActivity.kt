package com.sorametca.yet_another_launcher.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.sorametca.yet_another_launcher.GestureAction
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
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
    lateinit var appList: ArrayList<AppObject>
    private lateinit var validator: SettingsRepository

    // layout variables
    private lateinit var viewPager: ViewPager
    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    // helpers
    private lateinit var swipeListener: OnSwipeListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appList = getInstalledAppList(this)
        initializeHome()
        validator = SettingsRepository(this.dataStore, lifecycleScope)
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
    fun getInstalledAppList(context: Context): ArrayList<AppObject> {
        val resultList = ArrayList<AppObject>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val packageManager = context.packageManager
        val untreatedAppList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        for (untreatedApp in untreatedAppList) {
            val localPackageName = untreatedApp.activityInfo.packageName
            val label = untreatedApp.activityInfo.loadLabel(packageManager).toString()
            val icon = untreatedApp.activityInfo.loadIcon(packageManager)
            val app = AppObject(localPackageName, label, icon)
            if (!resultList.contains(app)) {
                resultList.add(app)
            }
        }

        return resultList
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        swipeListener.onTouch(null, ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun initializeHome() {
        val pagerAppList = ArrayList<PagerObject>()
        val homeAppList = ArrayList<AppObject>()
        pagerAppList.add(PagerObject(homeAppList))
        pagerAppList.add(PagerObject(homeAppList))
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

        val fragment = AppDrawerFragment(appList)
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