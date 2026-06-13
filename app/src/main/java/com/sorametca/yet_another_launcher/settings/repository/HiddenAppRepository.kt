package com.sorametca.yet_another_launcher.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.remove
import kotlin.collections.toMutableSet

class HiddenAppRepository (
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
){

    val HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")


    val hiddenApps: Flow<Set<String>> =
        dataStore.data.map {
            it[HIDDEN_APPS] ?: emptySet()
        }

    suspend fun hideApp(packageName: String?) {
        dataStore.edit { prefs ->
            val hidden =
                (prefs[HIDDEN_APPS] ?: emptySet()).toMutableSet()

            if (packageName != null) { // TODO it should not be null in the first place
                hidden.add(packageName)
            }

            prefs[HIDDEN_APPS] = hidden
        }
    }

    suspend fun unhideApp(packageName: String) {
        dataStore.edit { prefs ->
            val hidden =
                (prefs[HIDDEN_APPS] ?: emptySet()).toMutableSet()

            hidden.remove(packageName)

            prefs[HIDDEN_APPS] = hidden
        }
    }
}