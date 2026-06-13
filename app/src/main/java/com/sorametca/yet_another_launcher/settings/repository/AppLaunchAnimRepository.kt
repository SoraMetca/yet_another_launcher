package com.sorametca.yet_another_launcher.settings.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sorametca.yet_another_launcher.settings.dataStore
import com.sorametca.yet_another_launcher.settings.options.AppLaunchAnim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppLaunchAnimRepository(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    val APP_ANIM = stringPreferencesKey("app_anim")

    fun getLaunchAnim(context: Context): Flow<AppLaunchAnim> {
        return context.dataStore.data.map { prefs ->
            val savedValue = prefs[APP_ANIM]

            AppLaunchAnim.valueOf(savedValue ?: AppLaunchAnim.FADE_IN.name)
        }
    }

    suspend fun saveLaunchAnim(context: Context, anim: AppLaunchAnim) {
        context.dataStore.edit { prefs ->
            prefs[APP_ANIM] = anim.name
        }
    }
}