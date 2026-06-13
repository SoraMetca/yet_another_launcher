package com.sorametca.yet_another_launcher.model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.settings.RepositoryProvider.appSortRepository
import com.sorametca.yet_another_launcher.settings.RepositoryProvider.hiddenAppRepository
import com.sorametca.yet_another_launcher.settings.RepositoryProvider.labelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AppDrawerViewModel : ViewModel() {

    private val _appList = MutableStateFlow<List<AppObject>>(emptyList())
    val labelsFlow = labelRepository.labelsFlow
    val hiddenApps: Flow<Set<String>> = hiddenAppRepository.hiddenApps
    val appList = _appList.asStateFlow()
    private val _reorderMode = MutableStateFlow(false)
    val reorderMode = _reorderMode.asStateFlow()
    private var reorderBackup: List<AppObject>? = null
    private val installedAppsFlow = MutableStateFlow<List<AppObject>>(emptyList())

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {

            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val apps = pm.queryIntentActivities(intent, 0)
                .map {
                    AppObject(
                        it.activityInfo.packageName,
                        it.activityInfo.loadLabel(pm).toString(),
                        it.activityInfo.loadIcon(pm)
                    )
                }
                .distinctBy { it.packageName }

            installedAppsFlow.value = apps

            combine(
                installedAppsFlow,
                hiddenApps,
                appSortRepository.sortType,
                appSortRepository.customOrderFlow,
                _reorderMode
            ) { installed, hidden, sortType, customOrder, reorderMode ->

                if (reorderMode) {
                    return@combine _appList.value
                }

                val visible = installed.filter { it.packageName !in hidden }

                when (sortType) {
                    R.id.radio_alphabetical_asc ->
                        visible.sortedBy { it.label }

                    R.id.radio_alphabetical_desc ->
                        visible.sortedByDescending { it.label }

                    R.id.radio_custom -> {
                        val indexMap = customOrder.withIndex()
                            .associate { it.value to it.index }

                        visible.sortedBy { indexMap[it.packageName] ?: Int.MAX_VALUE }
                    }

                    else -> visible
                }
            }
                .collect {
                    _appList.value = it
                }
        }
    }

    fun getReorderMode(): Boolean {
        return _reorderMode.value
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
        reorderBackup = null
        _reorderMode.value = false
    }

    fun hideApp(packageName: String?) {
        viewModelScope.launch {
            hiddenAppRepository.hideApp(packageName)
        }
    }
}