package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private var installedAppList = ArrayList<AppObject>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeHome()

        val main = findViewById<View>(R.id.main)
        main.isClickable = true
        main.isFocusable = true

        viewPager.setOnTouchListener(
            object : OnSwipeListener(this, this) {}

        )
        main.setOnDragListener{v, event -> when(event.action){
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
        } }
    }

    private fun initializeHome() {
        var pagerAppList = ArrayList<PagerObject>()
        pagerAppList.add(PagerObject(homeAppList))
        viewPager = findViewById(R.id.ViewPager)
        mViewPagerAdapter = ViewPagerAdapter(this, pagerAppList)
        viewPager.setAdapter(mViewPagerAdapter)
    }

    private fun initializeDrawer() {
        val mBottomSheet: View = findViewById(R.id.bottomSheet)
        val mDrawerGridView: GridView = findViewById(R.id.drawerGrid)
        installedAppList = getinstalledAppList()
        mDrawerGridView.setAdapter(AppAdapter(getApplicationContext(), installedAppList))
    }
    private fun getinstalledAppList(): ArrayList<AppObject>{
        var list = ArrayList<AppObject>()

        var intent: Intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        var untreatedAppList: List<ResolveInfo> = getApplicationContext().
        getPackageManager().queryIntentActivities(intent, 0)

        for(untreatedapp in untreatedAppList){
            var appName: String = untreatedapp.activityInfo.loadLabel(getPackageManager()).toString()
            var appPackageName: String = untreatedapp.activityInfo.packageName
            var appImage: Drawable = untreatedapp.activityInfo.loadIcon(getPackageManager())
            var app: AppObject = AppObject(appPackageName, appName, appImage)
            if(!list.contains(app)){
                list.add(app)
            }
        }
        return list
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