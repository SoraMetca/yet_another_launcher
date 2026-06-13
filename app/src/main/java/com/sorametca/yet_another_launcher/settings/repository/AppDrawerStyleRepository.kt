package com.sorametca.yet_another_launcher.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sorametca.yet_another_launcher.settings.dataStore
import com.sorametca.yet_another_launcher.settings.options.AppDrawerStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDrawerStyleRepository(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    val APP_DRAWER_STYLE = stringPreferencesKey("app_drawer_style")


    fun getDrawerStyle(context: Context): Flow<AppDrawerStyle> {
        return context.dataStore.data.map { prefs ->
            val savedValue = prefs[APP_DRAWER_STYLE]

            AppDrawerStyle.valueOf(savedValue ?: AppDrawerStyle.GRID.name)
        }
    }

    suspend fun saveDrawerStyle(context: Context, style: AppDrawerStyle) {
        context.dataStore.edit { prefs ->
            prefs[APP_DRAWER_STYLE] = style.name
        }
    }
}