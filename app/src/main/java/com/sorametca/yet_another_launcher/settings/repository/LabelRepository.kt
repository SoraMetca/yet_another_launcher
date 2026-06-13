package com.sorametca.yet_another_launcher.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LabelRepository (
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    val labelsFlow: Flow<Map<String, String>> =
        dataStore.data.map { prefs ->
            prefs.asMap()
                .filterKeys { it.name.startsWith("label_") }
                .mapKeys { it.key.name.removePrefix("label_") }
                .mapValues { it.value as String }
        }

    suspend fun getLabel(packageName: String): String? {
        return labelsFlow.first()[packageName]
    }

    suspend fun setLabel(packageName: String, label: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("label_$packageName")] = label
        }
    }

    suspend fun removeLabel(packageName: String) { // TODO remove this, handle null label in context menu
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("label_$packageName"))
        }
    }
}