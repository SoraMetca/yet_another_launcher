package com.sorametca.yet_another_launcher.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sorametca.yet_another_launcher.settings.options.GestureAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// simple class for validating new settings, use changeGestureSettings,
// it can throw an error if new value is used in different gesture, confirm changes
// by overrideGestureSettings
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    object GestureKeys {
        val SWIPE_TO_TOP = intPreferencesKey("swipeToTop")
        val SWIPE_TO_BOTTOM = intPreferencesKey("swipeToBottom")
        val DOUBLE_TAP = intPreferencesKey("doubleTap")
        val LONG_PRESS = intPreferencesKey("longPress")
    }

    suspend fun saveGestureMap(map: Map<String, Int>) {
        dataStore.edit { prefs ->
            map["swipeToTop"]?.let { prefs[GestureKeys.SWIPE_TO_TOP] = it }
            map["swipeToBottom"]?.let { prefs[GestureKeys.SWIPE_TO_BOTTOM] = it }
            map["doubleTap"]?.let { prefs[GestureKeys.DOUBLE_TAP] = it }
            map["longPress"]?.let { prefs[GestureKeys.LONG_PRESS] = it }
        }
    }

    suspend fun loadGestureMap(): MutableMap<String, Int> {
        val prefs = dataStore.data.first()
        return mutableMapOf(
            "swipeToTop" to (prefs[GestureKeys.SWIPE_TO_TOP] ?: GestureAction.OPEN_APP_DRAWER.ordinal),
            "swipeToBottom" to (prefs[GestureKeys.SWIPE_TO_BOTTOM] ?: -1),
            "doubleTap" to (prefs[GestureKeys.DOUBLE_TAP] ?: GestureAction.OPEN_HOME_SCREEN_SETTINGS.ordinal),
            "longPress" to (prefs[GestureKeys.LONG_PRESS] ?: -1)
        )
    }

    var gestures = mutableMapOf(
        "swipeToTop" to GestureAction.OPEN_APP_DRAWER.ordinal,
        "swipeToBottom" to -1,
        "doubleTap" to GestureAction.OPEN_HOME_SCREEN_SETTINGS.ordinal,
        "longPress" to -1
    )
        private set

    init {
        scope.launch {
            gestures = loadGestureMap()
        }
    }

    fun changeGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict =
            gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            throw IllegalArgumentException("Value $newSettingValue is already used by ${conflict.key}")
        }
        gestures[settingName] = newSettingValue
        scope.launch { saveGestureMap(gestures) }
    }

    fun overrideGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict =
            gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            val currentTargetValue = gestures[settingName] ?: -1
            gestures[conflict.key] = currentTargetValue
        }
        gestures[settingName] = newSettingValue
        scope.launch { saveGestureMap(gestures) }
    }
}