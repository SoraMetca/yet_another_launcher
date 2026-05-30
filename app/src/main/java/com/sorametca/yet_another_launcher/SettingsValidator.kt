package com.sorametca.yet_another_launcher
import com.sorametca.yet_another_launcher.GestureAction

// simple class for validating new settings, use changeGestureSettings,
// it can throw an error if new value is used in different gesture, confirm changes
// by overrideGestureSettings
class SettingsValidator {
     val gestures = mutableMapOf(
        "swipeToTop" to GestureAction.openAppList.ordinal,
        "doubleTap" to GestureAction.openHomeScreenSettings.ordinal,
        "longPress" to -1
    )
    fun changeGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict = gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            throw IllegalArgumentException("Value $newSettingValue is already used by ${conflict.key}")
        }
        gestures[settingName] = newSettingValue
        }
    fun overrideGestureSettings(settingName: String, newSettingValue: Int) {
        val conflict = gestures.entries.find { it.value == newSettingValue && it.key != settingName }
        if (conflict != null) {
            val currentTargetValue = gestures[settingName] ?: -1
            gestures[conflict.key] = currentTargetValue
        }
        gestures[settingName] = newSettingValue
    }
}