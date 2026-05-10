package com.sorametca.yet_another_launcher.fragments

import com.sorametca.yet_another_launcher.model.AppObject

interface SortMethods {
    fun sort(appList: List<AppObject>): List<AppObject>
}