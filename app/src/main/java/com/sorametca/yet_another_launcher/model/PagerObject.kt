package com.sorametca.yet_another_launcher.model

class PagerObject(private var appList: ArrayList<AppObject>) {
    private val widgetIds = ArrayList<Int>()

    fun getAppList(): ArrayList<AppObject> {
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