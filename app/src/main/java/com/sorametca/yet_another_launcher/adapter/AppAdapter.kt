package com.sorametca.yet_another_launcher.adapter

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import java.lang.Math.abs

class AppAdapter(
    private val context: Context,
    private val appList: List<AppObject>
) : BaseAdapter() {

    override fun getCount(): Int = appList.size

    override fun getItem(position: Int): Any = appList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v: View
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_app, parent, false)
        } else {
            v = convertView
        }
        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)

        icon.setImageDrawable(appList[position].icon)
        label.text = appList[position].label

        val mLinearLayout = v.findViewById<LinearLayout>(R.id.Layout)
        mLinearLayout.setOnClickListener {
            (context as MainActivity).itemPress(appList[position])

        }

        var isLongPressed = false
        var dragStartX = 0f
        var dragStartY = 0f
        val DRAG_THRESHOLD = 15f

        mLinearLayout.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dragStartX = event.x
                    dragStartY = event.y
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isLongPressed) {
                        val dx = abs(event.x - dragStartX)
                        val dy = abs(event.y - dragStartY)
                        if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                            isLongPressed = false
                            val app = appList[position]
                            val clipData = ClipData.newPlainText("", "")
                            val shadowBuilder = View.DragShadowBuilder(v)
                            v.startDragAndDrop(clipData, shadowBuilder, app, 0)
                            (context as MainActivity).collapseFragment()
                        }
                    }
                    false
                }
                MotionEvent.ACTION_UP -> {
                    if (isLongPressed) {
                        isLongPressed = false
                        // TODO: context menu
                    }
                    false
                }
                else -> false
            }
        }

        mLinearLayout.setOnLongClickListener {
            isLongPressed = true
            true
        }
        return v
    }
}