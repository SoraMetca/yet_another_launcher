package com.sorametca.yet_another_launcher

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.AppDrawerStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        val DOUBLE_TAP = intPreferencesKey("doubleTap")
        val LONG_PRESS = intPreferencesKey("longPress")
    }

    val CUSTOM_ORDER = stringPreferencesKey("custom_order")
    val HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
    val SORT_TYPE_ID = intPreferencesKey("sort_type_id")
    val APP_ANIM = stringPreferencesKey("app_anim")
    val APP_DRAWER_STYLE = stringPreferencesKey("app_drawer_style")

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
            "doubleTap" to (prefs[GestureKeys.DOUBLE_TAP]
                ?: GestureAction.openHomeScreenSettings.ordinal),
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

    suspend fun saveCustomOrder(apps: List<AppObject>) {
        val order = apps.joinToString("|") { it.packageName }

        dataStore.edit { prefs ->
            prefs[CUSTOM_ORDER] = order
        }
    }

    val customOrderFlow: Flow<List<String>> =
        dataStore.data.map { prefs ->
            prefs[CUSTOM_ORDER]
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }

    val hiddenApps: Flow<Set<String>> =
        dataStore.data.map {
            it[HIDDEN_APPS] ?: emptySet()
        }

    val labelsFlow: Flow<Map<String, String>> =
        dataStore.data.map { prefs ->
            prefs.asMap()
                .filterKeys { it.name.startsWith("label_") }
                .mapKeys { it.key.name.removePrefix("label_") }
                .mapValues { it.value as String }
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

    suspend fun getLabel(packageName: String): String? {
        return labelsFlow.first()[packageName]
    }



    suspend fun setLabel(packageName: String, label: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("label_$packageName")] = label
        }
    }

    suspend fun removeLabel(packageName: String) {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("label_$packageName"))
        }
    }

    val appAnimType: Flow<Any> = dataStore.data
        .map { prefs ->
            prefs[APP_ANIM] ?: R.anim.fade_in
        }

    // app drawer styles

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