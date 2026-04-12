package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.GridView
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.OnSwipeListener
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter

class AppDrawerActivity : OnSwipeListener() {

    private var appList = ArrayList<AppObject>()
    private lateinit var packageName: String
    private lateinit var label: String
    private lateinit var icon: Drawable
    private lateinit var app: AppObject

    // layout
    private lateinit var mDrawerGridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_drawer)

        appList = getInstalledAppList()

        mDrawerGridView = findViewById(R.id.m_grid_view)
        mDrawerGridView.adapter = AppAdapter(applicationContext, appList)
    }

    override fun onSwipeToBottom() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun getInstalledAppList(): ArrayList<AppObject> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val untreatedAppList: List<ResolveInfo> =
            applicationContext.packageManager.queryIntentActivities(intent, 0)

        for (untreatedApp in untreatedAppList) {
            packageName = untreatedApp.activityInfo.packageName
            label = untreatedApp.activityInfo.loadLabel(packageManager).toString()
            icon = untreatedApp.activityInfo.loadIcon(packageManager)

            app = AppObject(packageName, label, icon)

            if (!appList.contains(app)) {
                appList.add(app)
            }
        }

        return appList
    }
}