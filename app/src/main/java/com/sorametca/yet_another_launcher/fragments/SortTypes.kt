package com.sorametca.yet_another_launcher.fragments

import com.sorametca.yet_another_launcher.model.AppObject

enum class SortTypes : SortMethods {
    ALPHABETICAL_ASC {
        override fun sort(appList: List<AppObject>): List<AppObject> {
            return appList.sortedBy { it.label.toString() }
        }
    },
    ALPHABETICAL_DESC {
        override fun sort(appList: List<AppObject>): List<AppObject> {
            return appList.sortedByDescending { it.label.toString() }
        }
    },
    CUSTOM { // TODO
        override fun sort(appList: List<AppObject>): List<AppObject> {
            return appList
        }
    }
}