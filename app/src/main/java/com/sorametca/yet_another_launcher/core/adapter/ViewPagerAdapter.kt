package com.sorametca.yet_another_launcher.core.adapter

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.sorametca.yet_another_launcher.model.PagerObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.model.LauncherItem
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.model.FolderObject


class ViewPagerAdapter(
    val context: Context,
    var pagerAppList: ArrayList<PagerObject>,
    private val appWidgetHost: AppWidgetHost
) : PagerAdapter() {

    private val activeGridAdapters = HashMap<Int, AppAdapter>()
    private val activePageViews = HashMap<Int, ViewGroup>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val layout: ViewGroup =
            inflater.inflate(R.layout.pager_layout, container, false) as ViewGroup

        val mGridView: GridView = layout.findViewById(R.id.grid)
        val mGridAdapter = AppAdapter(context, pagerAppList[position].getAppList())
        mGridView.adapter = mGridAdapter

        activeGridAdapters[position] = mGridAdapter
        activePageViews[position] = layout

        val pagerObject = pagerAppList[position]
        val widgetManager = AppWidgetManager.getInstance(context)
        for (widgetId in pagerObject.getWidgetIds()) {
            val appWidgetInfo = widgetManager.getAppWidgetInfo(widgetId)
            if (appWidgetInfo != null) {
                renderWidgetToLayout(layout, widgetId, appWidgetInfo, position)
            }
        }

        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        activePageViews.remove(position)
        activeGridAdapters.remove(position)
        container.removeView(`object` as View)
    }


    fun addWidgetToPage(position: Int, widgetId: Int, appWidgetInfo: AppWidgetProviderInfo?) {
        val layout = activePageViews[position]
        if (layout != null && appWidgetInfo != null) {
            renderWidgetToLayout(layout, widgetId, appWidgetInfo, position)
        }
    }

    private fun renderWidgetToLayout(
        layout: ViewGroup,
        widgetId: Int,
        appWidgetInfo: AppWidgetProviderInfo,
        position: Int
    ) {
        val widgetContainer =
            layout.findViewById<LinearLayout>(R.id.widget_container)

        val hostView: AppWidgetHostView =
            appWidgetHost.createView(context.applicationContext, widgetId, appWidgetInfo)
        hostView.setAppWidget(widgetId, appWidgetInfo)

        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        hostView.layoutParams = lp

        hostView.setOnLongClickListener {
            removeWidgetFromLauncher(position, widgetId, hostView, widgetContainer)
            true
        }

        widgetContainer?.addView(hostView)
    }

    private fun removeWidgetFromLauncher(
        position: Int,
        widgetId: Int,
        hostView: AppWidgetHostView,
        container: LinearLayout?
    ) {
        appWidgetHost.deleteAppWidgetId(widgetId)

        if (position < pagerAppList.size) {
            pagerAppList[position].removeWidget(widgetId)
        }

        container?.removeView(hostView)
    }

    override fun getCount(): Int = pagerAppList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    fun notifyGridChanged() {
        for (appAdapter in activeGridAdapters.values) {
            appAdapter.notifyDataSetChanged()
        }
    }

    fun addAppToPage(
        pagePosition: Int,
        draggedState: Any?,
        eventX: Float,
        eventY: Float,
        dragReceiverView: View
    ) {
        if (pagePosition >= pagerAppList.size) return

        val layout = activePageViews[pagePosition] ?: return
        val mGridView = layout.findViewById<GridView>(R.id.grid) ?: return

        val gridLocation = IntArray(2)
        mGridView.getLocationOnScreen(gridLocation)

        val localX = eventX - gridLocation[0]
        val localY = eventY - gridLocation[1]
        val numColumns = mGridView.numColumns
        val numRows = 6

        val cellWidth = mGridView.width / numColumns
        val cellHeight = mGridView.height / numRows

        val col = (localX / cellWidth).toInt().coerceIn(0, numColumns - 1)
        val row = (localY / cellHeight).toInt().coerceIn(0, numRows - 1)

        val targetPosition = (row * numColumns) + col
        val appList = pagerAppList[pagePosition].getAppList()

        if (targetPosition < 0 || targetPosition >= appList.size) return

        val draggedItem: LauncherItem
        var sourcePosition: Int? = null
        var isFromAppDrawer = false

        // Smart-deduce the source environment without touching MainActivity
        when (draggedState) {
            is Int -> {
                // Workspace reordering via grid position index
                sourcePosition = draggedState
                draggedItem = appList.getOrNull(sourcePosition) ?: return
                isFromAppDrawer = false
            }
            is DragAppData -> {
                draggedItem = draggedState.item
                sourcePosition = null
                isFromAppDrawer = true
            }
            is AppObject -> {
                draggedItem = draggedState
                val index = appList.indexOfFirst { it is AppObject && it.packageName == draggedItem.packageName }
                if (index != -1) {
                    sourcePosition = index
                    isFromAppDrawer = false
                } else {
                    sourcePosition = null
                    isFromAppDrawer = true
                }
            }
            is FolderObject -> {
                draggedItem = draggedState
                val index = appList.indexOfFirst { it is FolderObject && it.id == draggedItem.id }
                if (index != -1) {
                    sourcePosition = index
                    isFromAppDrawer = false
                }
            }
            else -> return
        }

        if (sourcePosition == targetPosition && !isFromAppDrawer) return

        val currentItemAtTarget = appList[targetPosition]

        when (currentItemAtTarget) {
            null -> {
                appList[targetPosition] = draggedItem
                if (sourcePosition != null && !isFromAppDrawer) {
                    appList[sourcePosition] = null
                }
            }
            is AppObject -> {
                if (draggedItem is AppObject && currentItemAtTarget.packageName != draggedItem.packageName) {
                    val newFolder = FolderObject(
                        id = "folder_${System.currentTimeMillis()}",
                        folderLabel = "Folder"
                    ).apply {
                        apps.add(currentItemAtTarget)
                        apps.add(draggedItem)
                    }
                    appList[targetPosition] = newFolder
                    if (sourcePosition != null && !isFromAppDrawer) {
                        appList[sourcePosition] = null
                    }
                }
            }
            is FolderObject -> {
                if (draggedItem is AppObject) {
                    val isAlreadyInFolder = currentItemAtTarget.apps.any { it.packageName == draggedItem.packageName }
                    if (!isAlreadyInFolder) {
                        currentItemAtTarget.apps.add(draggedItem)
                        if (sourcePosition != null && !isFromAppDrawer) {
                            appList[sourcePosition] = null
                        }
                    }
                }
            }
        }

        val adapter = mGridView.adapter as? AppAdapter
        adapter?.updateList(appList)

        mGridView.post {
            mGridView.invalidateViews()
        }
    }
}