package com.sorametca.yet_another_launcher.adapter

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.fragments.AppDrawerContextMenu
import com.sorametca.yet_another_launcher.model.AppObject
import java.util.ArrayList
import kotlin.math.abs

class AppAdapter(
    private val context: Context,
    private val originalList: ArrayList<AppObject>
) : BaseAdapter(), Filterable {

    private var filteredList: ArrayList<AppObject> = ArrayList(originalList)
    var labels: Map<String, String> = emptyMap()
    var reorderMode = false
    var draggedPosition = -1
    var hiddenApps: Set<String> = emptySet()

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): Any? = filteredList.getOrNull(position)
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val v: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_app, parent, false)
        } else {
            convertView
        }

        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)
        val mLinearLayout = v.findViewById<LinearLayout>(R.id.Layout)

        val app: AppObject? = filteredList.getOrNull(position)

        if (app == null) {
            mLinearLayout.visibility = View.INVISIBLE

            icon.setImageDrawable(null)
            label.text = ""

            mLinearLayout.setOnClickListener(null)
            mLinearLayout.setOnTouchListener(null)
            mLinearLayout.setOnLongClickListener(null)

        } else {
            mLinearLayout.visibility = View.VISIBLE

            if (app.icon != null) {
                icon.setImageDrawable(app.icon)
            } else {
                icon.setImageDrawable(context.packageManager.defaultActivityIcon)
            }

            label.text = displayLabel(app)

            mLinearLayout.setOnClickListener {
                if (!reorderMode) {
                    (context as? MainActivity)?.itemPress(app)
                }
            }

            var isLongPressed = false
            var dragStartX = 0f
            var dragStartY = 0f
            val DRAG_THRESHOLD = 15f

            mLinearLayout.setOnTouchListener { touchView, event ->
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
                                val clipData = ClipData.newPlainText("", "")
                                val shadowBuilder = View.DragShadowBuilder(touchView)
                                val dragData = DragAppData(app, position)
                                Log.d("LAUNCHER_DRAG", "1. Initiating Touch Drag from Adapter | App: ${app.label}, Position: $position")
                                touchView.startDragAndDrop(clipData, shadowBuilder, dragData, 0)
                                (context as? MainActivity)?.collapseFragment()
                            }
                        }
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isLongPressed) {
                            isLongPressed = false
                            val location = IntArray(2)
                            mLinearLayout.getLocationOnScreen(location)
                            val bundle = Bundle()
                            bundle.putInt("x", location[0])
                            bundle.putInt("y", location[1])
                            bundle.putString("packageName", app.packageName)

                            val fragment = AppDrawerContextMenu(filteredList)
                            fragment.arguments = bundle

                            val activity = context as? FragmentActivity
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.add(R.id.main, fragment)
                                ?.addToBackStack(null)
                                ?.commit()
                            true
                        } else {
                            false
                        }
                    }

                    else -> false
                }
            }

            mLinearLayout.setOnLongClickListener {
                isLongPressed = true

                if (!reorderMode)
                    return@setOnLongClickListener false

                draggedPosition = position

                val data = ClipData.newPlainText("", "")
                val dragData = DragAppData(app, position)

                it.startDragAndDrop(
                    data,
                    View.DragShadowBuilder(it),
                    dragData,
                    0
                )

                true
            }
        }

        return v
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = FilterResults()

                val filtered = originalList.filter {
                    it.packageName !in hiddenApps && displayLabel(it).lowercase().contains(query)
                }

                results.values = ArrayList(filtered)
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                filteredList = (results?.values as? ArrayList<AppObject>) ?: ArrayList(originalList)
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<AppObject?>) {
        val updatedList = newList.map { app ->
            if (app != null && isHidden(app.packageName)) {
                null
            } else {
                app
            }
        }

        this.filteredList = ArrayList(updatedList)
        Log.d("DEBUG_ADAPTER", "Adapter count is now: ${this.filteredList.size}")
        notifyDataSetChanged()
    }

    private fun isHidden(packageName: String): Boolean {
        return packageName in hiddenApps
    }

    private fun displayLabel(app: AppObject): String {
        return labels[app.packageName] ?: app.label
    }
}

data class DragAppData(
    val appObject: AppObject,
    val originPosition: Int
)