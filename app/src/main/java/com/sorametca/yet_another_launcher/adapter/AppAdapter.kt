package com.sorametca.yet_another_launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sorametca.yet_another_launcher.AppInfo
import com.sorametca.yet_another_launcher.R

class AppAdapter(
    private val context: Context,
    private val appList: List<AppInfo>
) : BaseAdapter() {

    override fun getCount(): Int = appList.size

    override fun getItem(position: Int): Any = appList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v: View
        if (convertView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_app, parent, false)
        }else{
            v = convertView
        }
            var icon: ImageView = v.findViewById(R.id.image)
            var label: TextView = v.findViewById(R.id.label)
        return null
    }
}