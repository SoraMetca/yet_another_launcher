package com.sorametca.yet_another_launcher.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.GestureAction
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.dataStore // Ensure your global DataStore is imported
import kotlinx.coroutines.launch

class GesturesFragment : Fragment() {

    private val items = listOf("None", "App Drawer", "Home Screen Settings", "Settings")

    private lateinit var swipeToTopSpinner: Spinner
    private lateinit var swipeToBottomSpinner: Spinner
    private lateinit var doubleTapSpinner: Spinner
    private lateinit var longPressSpinner: Spinner

    private lateinit var repository: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gestures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MainActivity.RepositoryProvider.settingsRepository

        swipeToTopSpinner = view.findViewById(R.id.swipe_to_top_spinner)
        swipeToBottomSpinner = view.findViewById(R.id.swipe_to_bottom_spinner)
        doubleTapSpinner = view.findViewById(R.id.double_tap_spinner)
        longPressSpinner = view.findViewById(R.id.long_press_spinner)

        val spinners = listOf(
            swipeToTopSpinner,
            swipeToBottomSpinner,
            doubleTapSpinner,
            longPressSpinner
        )

        attachAdapters(spinners)

        lifecycleScope.launch {
            val currentGestures = repository.loadGestureMap()

            swipeToTopSpinner.setSelection(getActionIndex(currentGestures["swipeToTop"]))
            swipeToBottomSpinner.setSelection(getActionIndex(currentGestures["swipeToBottom"]))
            doubleTapSpinner.setSelection(getActionIndex(currentGestures["doubleTap"]))
            longPressSpinner.setSelection(getActionIndex(currentGestures["longPress"]))

            itemSelectSetup(spinners)
        }
    }

    private fun attachAdapters(spinners: List<Spinner>) {
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, items
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        for (spinner in spinners) {
            spinner.adapter = adapter
        }
    }

    private fun itemSelectSetup(spinners: List<Spinner>) {
        for (spinner in spinners) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedActionString = parent?.getItemAtPosition(position) as String
                    val newValue = getActionValue(selectedActionString)
                    val gestureKey = getGestureKeyForSpinner(spinner)

                    if (repository.gestures[gestureKey] == newValue) return

                    try {
                        repository.changeGestureSettings(gestureKey, newValue)
                    } catch (e: IllegalArgumentException) {
                        showConflictDialog(spinner, gestureKey, newValue, e.message)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showConflictDialog(spinner: Spinner, gestureKey: String, newValue: Int, message: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Action Already in Use")
            .setMessage("$message\n\nDo you want to swap them?")
            .setPositiveButton("Swap") { _, _ ->
                repository.overrideGestureSettings(gestureKey, newValue)

                refreshAllSpinners()
            }
            .setNegativeButton("Cancel") { _, _ ->
                val previousValue = repository.gestures[gestureKey]
                spinner.setSelection(getActionIndex(previousValue))
            }
            .setCancelable(false)
            .show()
    }

    private fun refreshAllSpinners() {
        swipeToTopSpinner.setSelection(getActionIndex(repository.gestures["swipeToTop"]))
        swipeToBottomSpinner.setSelection(getActionIndex(repository.gestures["swipeToBottom"]))
        doubleTapSpinner.setSelection(getActionIndex(repository.gestures["doubleTap"]))
        longPressSpinner.setSelection(getActionIndex(repository.gestures["longPress"]))
    }
    private fun getActionValue(actionString: String): Int {
        return when (actionString) {
            "App Drawer" -> GestureAction.openAppList.ordinal
            "Home Screen Settings" -> GestureAction.openHomeScreenSettings.ordinal
            "Settings" -> GestureAction.openSettings.ordinal
            "None" -> -1
            else -> -1
        }
    }

    private fun getActionIndex(actionValue: Int?): Int {
        if (actionValue == null || actionValue == -1) return items.indexOf("None")

        return when (actionValue) {
            GestureAction.openAppList.ordinal           -> items.indexOf("App Drawer")
            GestureAction.openHomeScreenSettings.ordinal -> items.indexOf("Home Screen Settings")
            GestureAction.openSettings.ordinal           -> items.indexOf("Settings")
            else                                         -> items.indexOf("None")
        }
    }
    private fun getGestureKeyForSpinner(spinner: Spinner): String {
        return when (spinner.id) {
            R.id.swipe_to_top_spinner -> "swipeToTop"
            R.id.swipe_to_bottom_spinner -> "swipeToBottom"
            R.id.double_tap_spinner -> "doubleTap"
            R.id.long_press_spinner -> "longPress"
            else -> ""
        }
    }
}