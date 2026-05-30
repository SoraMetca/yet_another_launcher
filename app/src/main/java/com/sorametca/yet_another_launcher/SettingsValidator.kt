package com.sorametca.yet_another_launcher
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sorametca.yet_another_launcher.GestureAction
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// simple class for validating new settings, use changeGestureSettings,
// it can throw an error if new value is used in different gesture, confirm changes
// by overrideGestureSettings
class SettingsValidator(private val dataStore: DataStore<Preferences>, private val scope: CoroutineScope){

    object GestureKeys {
        val SWIPE_TO_TOP = intPreferencesKey("swipeToTop")
        val DOUBLE_TAP = intPreferencesKey("doubleTap")
        val LONG_PRESS = intPreferencesKey("longPress")
    }
    suspend fun saveGestureMap(map: Map<String, Int>) {
        dataStore.edit { prefs ->
            map["swipeToTop"]?.let { prefs[GestureKeys.SWIPE_TO_TOP] = it }
            map["doubleTap"]?.let { prefs[GestureKeys.DOUBLE_TAP] = it }
            map["longPress"]?.let { prefs[GestureKeys.LONG_PRESS] = it }
        }
    }
    suspend fun loadGestureMap(): MutableMap<String, Int> {
        val prefs = dataStore.data.first()
        return mutableMapOf(
            "swipeToTop" to (prefs[GestureKeys.SWIPE_TO_TOP] ?: GestureAction.openAppList.ordinal),
            "doubleTap" to (prefs[GestureKeys.DOUBLE_TAP] ?: GestureAction.openHomeScreenSettings.ordinal),
            "longPress" to (prefs[GestureKeys.LONG_PRESS] ?: -1)
        )
    }
    var gestures = mutableMapOf(
        "swipeToTop" to GestureAction.openAppList.ordinal,
        "doubleTap" to GestureAction.openHomeScreenSettings.ordinal,
        "longPress" to -1
    )
        private set

    init {
        scope.launch {
            gestures = loadGestureMap()
        }
    }
    fun changeGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict = gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            throw IllegalArgumentException("Value $newSettingValue is already used by ${conflict.key}")
        }
        gestures[settingName] = newSettingValue
        scope.launch { saveGestureMap(gestures) }
        }
    fun overrideGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict = gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            val currentTargetValue = gestures[settingName] ?: -1
            gestures[conflict.key] = currentTargetValue
        }
        gestures[settingName] = newSettingValue
        scope.launch { saveGestureMap(gestures) }
    }
}