package com.sorametca.yet_another_launcher.model

import com.sorametca.yet_another_launcher.interfaces.LauncherItem

class PagerObject(private var appList: ArrayList<LauncherItem?>) {
    private val widgetIds = ArrayList<Int>()

    fun getAppList(): ArrayList<LauncherItem?> {
        return appList
    }

    fun getWidgetIds(): ArrayList<Int> {
        return widgetIds
    }

    fun addWidget(widgetId: Int) {
        widgetIds.add(widgetId)
    }
    fun removeWidget(widgetId: Int) {
        widgetIds.remove(widgetId)
    }
}