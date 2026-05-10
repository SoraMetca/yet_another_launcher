package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.model.PagerObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.adapter.ViewPagerAdapter
import com.sorametca.yet_another_launcher.fragments.AppDrawerFragment
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener

class MainActivity : AppCompatActivity(), OnSwipeInterface {
    private lateinit var viewPager: ViewPager
    private var installedAppList = ArrayList<AppObject>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeHome()

        val main = findViewById<View>(R.id.main)
        main.isClickable = true
        main.isFocusable = true

        main.setOnTouchListener(
            object : OnSwipeListener(this, this) {}
        )
    }

    private fun initializeHome() {
        val pagerAppList = ArrayList<PagerObject>()

        viewPager = findViewById(R.id.ViewPager)
        viewPager.setAdapter(ViewPagerAdapter(this, pagerAppList))
    }

//    private fun initializeDrawer() {
//        val mBottomSheet: View = findViewById(R.id.bottomSheet)
//        val mDrawerGridView: GridView = findViewById(R.id.drawerGrid)
//        installedAppList = getinstalledAppList()
//        mDrawerGridView.adapter = AppAdapter(applicationContext, installedAppList)
//    }

    private fun getinstalledAppList(): ArrayList<AppObject> {
        val list = ArrayList<AppObject>()

        var appName: String
        var appPackageName: String
        var appImage: Drawable
        var app: AppObject

        val intent: Intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val untreatedAppList: List<ResolveInfo> =
            applicationContext.packageManager.queryIntentActivities(intent, 0)

        for (untreatedapp in untreatedAppList) {
            appName = untreatedapp.activityInfo.loadLabel(packageManager).toString()
            appPackageName = untreatedapp.activityInfo.packageName
            appImage = untreatedapp.activityInfo.loadIcon(packageManager)
            app = AppObject(appPackageName, appName, appImage)
            if (!list.contains(app)) {
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
}