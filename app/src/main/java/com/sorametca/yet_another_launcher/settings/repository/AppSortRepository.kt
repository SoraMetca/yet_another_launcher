package com.sorametca.yet_another_launcher.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppSortRepository (
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {
    val SORT_TYPE_ID = intPreferencesKey("sort_type_id")
    val CUSTOM_ORDER = stringPreferencesKey("custom_order")


    val sortType: Flow<Int> = dataStore.data
        .map { prefs ->
            prefs[SORT_TYPE_ID] ?: R.id.radio_alphabetical_asc
        }

    suspend fun getSavedSortMethodId(context: Context): Int {
        return context.dataStore.data
            .map { prefs ->
                prefs[SORT_TYPE_ID] ?: R.id.radio_alphabetical_asc
            }
            .first()
    }

    suspend fun saveSortMethodId(context: Context, checkedId: Int) {
        context.dataStore.edit { prefs ->
            prefs[SORT_TYPE_ID] = checkedId
        }
    }

    val customOrderFlow: Flow<List<String>> =
        dataStore.data.map { prefs ->
            prefs[CUSTOM_ORDER]
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }

    suspend fun saveCustomOrder(apps: List<AppObject>) {
        val order = apps.joinToString("|") { it.packageName }

        dataStore.edit { prefs ->
            prefs[CUSTOM_ORDER] = order
        }
    }
}