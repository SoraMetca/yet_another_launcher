package com.sorametca.yet_another_launcher.adapter

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.fragments.AppDrawerContextMenu
import com.sorametca.yet_another_launcher.model.AppObject
import kotlin.math.abs
import android.util.Log

class AppAdapter(
    private val context: Context,
    private val originalList: ArrayList<AppObject>
) : BaseAdapter(), Filterable {

    private var filteredList: ArrayList<AppObject> = originalList
    var labels: Map<String, String> = emptyMap()
    var reorderMode = false
    var draggedPosition = -1

    override fun getCount(): Int = filteredList.size
    override fun getItem(position: Int): Any = filteredList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View = if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.item_app, parent, false)
        } else {
            convertView
        }

        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)
        icon.setImageDrawable(filteredList[position].icon)
        val app = filteredList[position]
        label.text = displayLabel(app)

        val mLinearLayout = v.findViewById<LinearLayout>(R.id.Layout)

        mLinearLayout.setOnClickListener {
            if(!reorderMode) {
                (context as MainActivity).itemPress(filteredList[position])
            }
        }

        var isLongPressed = false
        var dragStartX = 0f
        var dragStartY = 0f
        val DRAG_THRESHOLD = 15f
        mLinearLayout.setOnTouchListener { v, event -> // TODO supress warning & move to method
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
                            val app = filteredList[position]
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
                        val location = IntArray(2)
                        mLinearLayout.getLocationOnScreen(location)
                        val bundle = Bundle()
                        bundle.putInt("x", location[0])
                        bundle.putInt("y", location[1])
                        bundle.putString("packageName", filteredList[position].packageName)

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

            it.startDragAndDrop(
                data,
                View.DragShadowBuilder(it),
                position,
                0
            )

            true
        }

        return v
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""

                val results = FilterResults()
                results.values = originalList.filter {
                    it.packageName !in hiddenApps &&
                            displayLabel(it).lowercase().contains(query)
                } as ArrayList<AppObject>

                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                filteredList = results?.values as? ArrayList<AppObject>
                    ?: originalList

                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<AppObject>) {
        filteredList = ArrayList(newList.filter { !isHidden(it.packageName) })
        notifyDataSetChanged()
    }
    var hiddenApps: Set<String> = emptySet()

    private fun isHidden(packageName: String): Boolean {
        return packageName in hiddenApps
    }

    private fun displayLabel(app: AppObject): String {
        return labels[app.packageName] ?: app.label
    }
}