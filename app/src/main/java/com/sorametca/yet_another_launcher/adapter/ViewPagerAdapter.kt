package com.sorametca.yet_another_launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.viewpager.widget.PagerAdapter
import com.sorametca.yet_another_launcher.PagerObject
import com.sorametca.yet_another_launcher.R

class ViewPagerAdapter(val context: Context, var pagerAppList: ArrayList<PagerObject>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val layout: ViewGroup = inflater.inflate(R.layout.pager_layout, container, false) as ViewGroup

        val gridView: GridView = layout.findViewById(R.id.grid)
        gridView.setAdapter(AppAdapter(context, pagerAppList[position].getAppList()))

        container.addView(layout)
        return layout
    }

    override fun getCount(): Int {
        return pagerAppList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}