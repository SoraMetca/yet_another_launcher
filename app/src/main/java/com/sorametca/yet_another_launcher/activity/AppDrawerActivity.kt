package com.sorametca.yet_another_launcher.activity

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.OnSwipeListener
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter

class AppDrawerActivity : OnSwipeListener() {

    private var appList = ArrayList<AppObject>()
    private lateinit var mDrawerGridView : GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_drawer)

        appList = ArrayList()

        mDrawerGridView = findViewById(R.id.m_grid_view)

        for (i in 1..20){
            appList.add(AppObject("", i.toString(), resources.getDrawable(R.drawable.ic_launcher_background)))
        }
        mDrawerGridView.adapter = AppAdapter(applicationContext, appList)
    }

    override fun onSwipeToBottom() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}