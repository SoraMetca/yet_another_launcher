package com.sorametca.yet_another_launcher.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sorametca.yet_another_launcher.settings.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatusBarRepository(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    val STATUS_BAR_TOGGLE = booleanPreferencesKey("status_bar_toggle")


    val statusBarToggle: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[STATUS_BAR_TOGGLE] ?: false
        }

    fun getStatusBarToggle(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[STATUS_BAR_TOGGLE] ?: false
        }
    }

    suspend fun saveStatusBarToggle(context: Context, state: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[STATUS_BAR_TOGGLE] = state
        }
    }
}