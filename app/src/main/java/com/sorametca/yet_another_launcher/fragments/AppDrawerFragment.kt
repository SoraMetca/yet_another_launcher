package com.sorametca.yet_another_launcher.fragments

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface

class AppDrawerFragment : Fragment(), OnSwipeInterface {

    private var appList = ArrayList<AppObject>()
    private lateinit var packageName: String
    private lateinit var label: String
    private lateinit var icon: Drawable
    private lateinit var app: AppObject
    private var startY = 0f

    // layout
    private lateinit var mDrawerGridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        appList = getInstalledAppList()

        mDrawerGridView = view.findViewById(R.id.m_grid_view)
        mDrawerGridView.adapter = AppAdapter(requireContext(), appList)

        mDrawerGridView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startY = event.y

                MotionEvent.ACTION_MOVE -> {
                    val diffY = event.y - startY
                    print(diffY)
                    if (diffY > 80 && mDrawerGridView.isAtTop()) {
                        requireActivity().supportFragmentManager.popBackStack()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

    }

    fun GridView.isAtTop(): Boolean {
        return firstVisiblePosition == 0 &&
                (getChildAt(0)?.top ?: 0) >= 0
    }


    fun getInstalledAppList(): ArrayList<AppObject> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val untreatedAppList: List<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(intent, 0)

        for (untreatedApp in untreatedAppList) {
            packageName = untreatedApp.activityInfo.packageName
            label = untreatedApp.activityInfo.loadLabel(requireContext().packageManager).toString()
            icon = untreatedApp.activityInfo.loadIcon(requireContext().packageManager)

            app = AppObject(packageName, label, icon)

            if (!appList.contains(app)) {
                appList.add(app)
            }
        }

        return appList
    }
}