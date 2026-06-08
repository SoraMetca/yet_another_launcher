package com.sorametca.yet_another_launcher.model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsValidator
import kotlinx.coroutines.launch

class AppDrawerViewModel : ViewModel() {

    private val _appList = MutableLiveData<List<AppObject>>(emptyList())
    private val _reorderMode = MutableLiveData(false)
    val reorderMode: LiveData<Boolean> = _reorderMode
    private var reorderBackup: List<AppObject>? = null
    val appList: LiveData<List<AppObject>> = _appList
    private val _sortTypeId = MutableLiveData(R.id.radio_alphabetical_asc)

    fun loadInstalledApps(context: Context, settingsValidator: SettingsValidator) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        viewModelScope.launch {
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
                R.id.radio_custom -> settingsValidator.applyCustomOrder(apps)
                else -> apps
            }

            _appList.value = apps
            _sortTypeId.value = sortId
        }
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
        val hidden =
            prefs.getStringSet("hidden_apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        hidden.add(packageName)
        prefs.edit().putStringSet("hidden_apps", hidden).apply()

        _appList.value = _appList.value?.filter { it.packageName != packageName }
    }


    fun getReorderMode(): Boolean {
        return _reorderMode.value == true
    }

    fun setReorderMode(enabled: Boolean) {
        _reorderMode.value = enabled
    }

    fun startReorder() {
        reorderBackup = _appList.value?.toList()
        _reorderMode.value = true
    }

    fun moveApp(from: Int, to: Int) {
        val list = _appList.value?.toMutableList() ?: return

        val item = list.removeAt(from)
        list.add(to, item)

        _appList.value = list
    }

    fun cancelReorder() {
        reorderBackup?.let {
            _appList.value = it
        }

        reorderBackup = null
        _reorderMode.value = false
    }
}