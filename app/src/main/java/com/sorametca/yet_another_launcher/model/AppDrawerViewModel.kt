package com.sorametca.yet_another_launcher.model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sorametca.yet_another_launcher.R

class AppDrawerViewModel : ViewModel() {

    private val _appList = MutableLiveData<List<AppObject>>(emptyList())
    val appList: LiveData<List<AppObject>> = _appList

    private val _sortTypeId = MutableLiveData(R.id.radio_alphabetical_asc)

    fun loadInstalledApps(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val pm = context.packageManager
        var apps = pm.queryIntentActivities(intent, 0)
            .map {
                AppObject(
                    it.activityInfo.packageName,
                    it.activityInfo.loadLabel(pm).toString(),
                    it.activityInfo.loadIcon(pm)
                )
            }
            .distinctBy { it.packageName }

        val hidden = getHiddenApps(context)
        apps = apps.filter { it.packageName !in hidden }

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val sortId = prefs.getInt("sort_type_id", R.id.radio_alphabetical_asc)
        apps = when (sortId) {
            R.id.radio_alphabetical_asc -> apps.sortedBy { it.label }
            R.id.radio_alphabetical_desc -> apps.sortedByDescending { it.label }
            R.id.radio_custom -> apps
            else -> apps
        }

        _appList.value = apps
        _sortTypeId.value = sortId
    }

    fun updateAppList(newList: List<AppObject>) {
        _appList.value = newList
    }

    fun setSortType(sortId: Int) {
        _sortTypeId.value = sortId
    }

    private fun getHiddenApps(context: Context): Set<String> {
        val prefs = context.getSharedPreferences("app_drawer_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()
    }

    fun hideApp(context: Context, packageName: String) {
        val prefs = context.getSharedPreferences("app_drawer_prefs", Context.MODE_PRIVATE)
        val hidden = prefs.getStringSet("hidden_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        hidden.add(packageName)
        prefs.edit().putStringSet("hidden_apps", hidden).apply()

        _appList.value = _appList.value?.filter { it.packageName != packageName }
    }
}