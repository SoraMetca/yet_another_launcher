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
import com.sorametca.yet_another_launcher.interfaces.LauncherItem
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.AppDrawerStyle
import com.sorametca.yet_another_launcher.model.FolderObject
import java.util.ArrayList
import kotlin.math.abs

class AppAdapter(
    private val context: Context,
    private val originalList: ArrayList<LauncherItem?>
) : BaseAdapter(), Filterable {

    private var filteredList: ArrayList<LauncherItem?> = ArrayList(originalList)
    var labels: Map<String, String> = emptyMap()
    var reorderMode = false
    var draggedPosition = -1
    var hiddenApps: Set<String> = emptySet()

    override fun getCount(): Int = filteredList.size

    private var drawerStyle = AppDrawerStyle.GRID

    fun setDrawerStyle(style: AppDrawerStyle) {
        if (drawerStyle != style) {
            drawerStyle = style
            notifyDataSetChanged()
        }
    }

    override fun getItem(position: Int): Any? = filteredList.getOrNull(position)
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutId = when (drawerStyle) {
            AppDrawerStyle.GRID -> R.layout.app_grid_item
            AppDrawerStyle.LIST -> R.layout.app_list_item
        }

        val v: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(layoutId, parent, false)
        } else {
            convertView
        }

        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)
        val mLinearLayout = v.findViewById<LinearLayout>(R.id.Layout)

        val item: LauncherItem? = filteredList.getOrNull(position)

        mLinearLayout.setOnClickListener(null)
        mLinearLayout.setOnTouchListener(null)
        mLinearLayout.setOnLongClickListener(null)

        when (item) {
            null -> {
                mLinearLayout.visibility = View.INVISIBLE
                icon.setImageDrawable(null)
                label.text = ""
            }
            is AppObject -> {
                mLinearLayout.visibility = View.VISIBLE
                icon.setImageDrawable(item.icon ?: context.packageManager.defaultActivityIcon)
                label.text = displayLabel(item)

                mLinearLayout.setOnClickListener {
                    if (!reorderMode) (context as? MainActivity)?.itemPress(item)
                }
                setupDragListeners(mLinearLayout, item, position)
            }
            is FolderObject -> {
                mLinearLayout.visibility = View.VISIBLE

                icon.setImageResource(android.R.drawable.ic_menu_today)
                label.text = item.folderLabel

                mLinearLayout.setOnClickListener {
                    if (!reorderMode) (context as? MainActivity)?.openFolder(item)
                }
                setupDragListeners(mLinearLayout, item, position)
            }
        }

        return v
    }

    private fun setupDragListeners(view: View, item: LauncherItem, position: Int) {
        var isLongPressed = false
        var dragStartX = 0f
        var dragStartY = 0f
        val DRAG_THRESHOLD = 15f

        view.setOnTouchListener { touchView, event ->
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
                            val dragData = DragAppData(item, position)
                            touchView.startDragAndDrop(clipData, shadowBuilder, dragData, 0)
                            (context as? MainActivity)?.collapseFragment()
                        }
                        if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                            isLongPressed = false
                            val clipData = ClipData.newPlainText("", "")
                            val shadowBuilder = View.DragShadowBuilder(touchView)
                            val dragData = DragAppData(item, position)

                            touchView.startDragAndDrop(clipData, shadowBuilder, dragData, 0)

                            (context as? MainActivity)?.collapseFragment()

                            (context as? MainActivity)?.closeFolder()
                        }
                    }

                    false
                }
                MotionEvent.ACTION_UP -> {
                    if (isLongPressed && item is AppObject) {
                        isLongPressed = false
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)
                        val bundle = Bundle().apply {
                            putInt("x", location[0])
                            putInt("y", location[1])
                            putString("packageName", item.packageName)
                        }

                        val appsOnlyList = ArrayList(filteredList.filterIsInstance<LauncherItem?>())
                        val fragment = AppDrawerContextMenu(appsOnlyList)
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

        view.setOnLongClickListener {
            isLongPressed = true
            if (!reorderMode) return@setOnLongClickListener false

            draggedPosition = position
            val data = ClipData.newPlainText("", "")
            val dragData = DragAppData(item, position)

            it.startDragAndDrop(data, View.DragShadowBuilder(it), dragData, 0)
            true
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = FilterResults()

                val filtered = originalList.filterNotNull().filter { item ->
                    when (item) {
                        is AppObject -> item.packageName !in hiddenApps && displayLabel(item).lowercase().contains(query)
                        is FolderObject -> item.folderLabel.lowercase().contains(query)
                        else -> false
                    }
                }

                results.values = ArrayList(filtered)
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? ArrayList<LauncherItem?>) ?: ArrayList(originalList)
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<LauncherItem?>) {
        val updatedList = newList.map { item ->
            if (item is AppObject && isHidden(item.packageName)) {
                null
            } else {
                item
            }
        }

        this.filteredList = ArrayList(updatedList)
        Log.d("DEBUG_ADAPTER", "Adapter count is now: ${this.filteredList.size}")
        notifyDataSetChanged()
    }

    private fun isHidden(packageName: String): Boolean = packageName in hiddenApps

    private fun displayLabel(app: AppObject): String = labels[app.packageName] ?: app.label
}

data class DragAppData(
    val item: LauncherItem,
    val originPosition: Int
)