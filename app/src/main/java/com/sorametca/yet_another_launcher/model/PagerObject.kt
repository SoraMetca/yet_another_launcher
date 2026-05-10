package com.sorametca.yet_another_launcher.model

class PagerObject(private var appList: ArrayList<AppObject>) {

    fun getAppList(): ArrayList<AppObject> {
        return appList
    }
}