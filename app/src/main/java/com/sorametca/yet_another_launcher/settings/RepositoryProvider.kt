package com.sorametca.yet_another_launcher.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sorametca.yet_another_launcher.settings.repository.AppDrawerStyleRepository
import com.sorametca.yet_another_launcher.settings.repository.AppLaunchAnimRepository
import com.sorametca.yet_another_launcher.settings.repository.AppSortRepository
import com.sorametca.yet_another_launcher.settings.repository.HiddenAppRepository
import com.sorametca.yet_another_launcher.settings.repository.LabelRepository
import com.sorametca.yet_another_launcher.settings.repository.SettingsRepository
import com.sorametca.yet_another_launcher.settings.repository.StatusBarRepository
import kotlinx.coroutines.CoroutineScope

object RepositoryProvider {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var scope: CoroutineScope

    fun initialize(
        dataStore: DataStore<Preferences>,
        scope: CoroutineScope
    ) {
        this.dataStore = dataStore
        this.scope = scope
    }

    val settingsRepository by lazy {
        SettingsRepository(dataStore, scope)
    }

    val appDrawerStyleRepository by lazy {
        AppDrawerStyleRepository(dataStore, scope)
    }

    val appSortRepository by lazy {
        AppSortRepository(dataStore, scope)
    }

    val hiddenAppRepository by lazy {
        HiddenAppRepository(dataStore, scope)
    }

    val labelRepository by lazy {
        LabelRepository(dataStore, scope)
    }

    val appLaunchAnimRepository by lazy {
        AppLaunchAnimRepository(dataStore, scope)
    }

    val statusBarRepository by lazy {
        StatusBarRepository(dataStore, scope)
    }
}