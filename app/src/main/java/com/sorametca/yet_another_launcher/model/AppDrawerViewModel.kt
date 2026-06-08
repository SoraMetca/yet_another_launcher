package com.sorametca.yet_another_launcher.model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppDrawerViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _appList = MutableStateFlow<List<AppObject>>(emptyList())
    val labelsFlow = repository.labelsFlow
    val hiddenApps: Flow<Set<String>> = repository.hiddenApps
    val appList = _appList.asStateFlow()
    private val _reorderMode = MutableStateFlow(false)
    val reorderMode = _reorderMode.asStateFlow()
    private var reorderBackup: List<AppObject>? = null
    private var installedApps: List<AppObject> = emptyList()

    fun loadInstalledApps(context: Context, settingsRepository: SettingsRepository) {

        viewModelScope.launch {

            val pm = context.packageManager

            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            installedApps = pm.queryIntentActivities(intent, 0)
                .map {
                    AppObject(
                        it.activityInfo.packageName,
                        it.activityInfo.loadLabel(pm).toString(),
                        it.activityInfo.loadIcon(pm)
                    )
                }
                .distinctBy { it.packageName }

            combine(repository.hiddenApps, repository.sortType) { hiddenApps, sortType -> var apps = installedApps.filter { it.packageName !in hiddenApps }

                apps
            }.collect {
                _appList.value = it
            }
        }
    }

    fun updateAppList(newList: List<AppObject>) {
        _appList.value = newList
    }

    fun setSortType(sortId: Int) {
        viewModelScope.launch {
            repository.setSortType(sortId)
        }
    }

    fun getReorderMode(): Boolean {
        return _reorderMode.value == true
    }

    fun setReorderMode(enabled: Boolean) {
        _reorderMode.value = enabled
    }

    fun startReorder() {
        reorderBackup = _appList.value.toList()
        _reorderMode.value = true
    }

    fun moveApp(from: Int, to: Int) {
        val list = _appList.value.toMutableList()

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

    fun hideApp(packageName: String?) {
        viewModelScope.launch {
            repository.hideApp(packageName)
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch {
            repository.unhideApp(packageName)
        }
    }
}