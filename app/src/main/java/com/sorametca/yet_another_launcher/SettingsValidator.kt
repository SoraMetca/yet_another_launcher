package com.sorametca.yet_another_launcher
import com.sorametca.yet_another_launcher.GestureAction
class SettingsValidator {
    var swipeToTop: Int? = null
    var doubleTap: Int? = null
    var longPress: Int? = null

    constructor() {
        this.swipeToTop = GestureAction.openAppList.ordinal
        this.doubleTap = GestureAction.openHomeScreenSettings.ordinal
    }
    fun changeGestureSettings(settingName: String, newSettingValue: Int) {
        when (settingName) {
            "swipeToTop" -> {
                if (newSettingValue == doubleTap) {
                    throw error("gesture already in use")
                } else {
                    swipeToTop = newSettingValue
                }
            }
            "doubleTap" -> {
                if (newSettingValue == swipeToTop) {
                    throw error("gesture already in use")
                } else {
                    doubleTap = newSettingValue
                }
            }
        }
    }
}