package com.sorametca.yet_another_launcher.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class ProfilesFragment : Fragment() {

    private val repository = MainActivity.RepositoryProvider.settingsRepository

    private lateinit var manualExport: TextView
    private lateinit var importProfiles: TextView
    private lateinit var autoExportSwitch: Switch
    private lateinit var automaticExportSwitch: Switch
    private lateinit var exportStatusText: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manualExport = view.findViewById(R.id.manual_export)
        importProfiles = view.findViewById(R.id.import_profiles)
        autoExportSwitch = view.findViewById(R.id.automatic_export_switch)
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getAutoExportToggle(requireContext()).collect { state ->
                autoExportSwitch.isChecked = state
            }
        }
        automaticExportSwitch = view.findViewById(R.id.automatic_export_switch)
        exportStatusText = view.findViewById(R.id.export_status)
        manualExport.setOnClickListener { _: View? ->
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            exportLauncher.launch("YAL_Profile_$dateString.json")
        }
        importProfiles.setOnClickListener { _: View? ->
            importLauncher.launch(arrayOf("application/json"))
        }

        automaticExportSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            Log.d("option clicked", (if (isChecked) "enabled" else "disabled"))
        }
    }

    fun exportProfileData(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val preferencesMap = requireContext().dataStore.data.first().asMap()

                val preferencesJson = JSONObject()
                for ((key, value) in preferencesMap) {
                    preferencesJson.put(key.name, value)
                }

                val timestamp = System.currentTimeMillis()
                val rootJson = JSONObject().apply {
                    put("backup_schema_version", 1)
                    put("timestamp", timestamp)
                    put("preferences", preferencesJson)
                }

                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(rootJson.toString(4).toByteArray())
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Profile Exported!", Toast.LENGTH_SHORT)
                        .show()
                    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(
                        Date(timestamp)
                    )
                    exportStatusText.text = "Most recent export successful on $dateStr"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Export Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun importProfileData(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val jsonString = requireContext().contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.use { it.readText() }

                if (jsonString.isNullOrEmpty()) throw Exception("File is empty or unreadable")

                val rootJson = JSONObject(jsonString)
                val schemaVersion = rootJson.optInt("backup_schema_version", -1)

                if (schemaVersion == -1 || schemaVersion > 1) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Incompatible backup version.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@launch
                }

                val preferencesJson = rootJson.getJSONObject("preferences")
                val keys = preferencesJson.keys()


                requireContext().dataStore.edit { mutablePreferences ->
                    while (keys.hasNext()) {
                        val keyName = keys.next()
                        when (val value = preferencesJson.get(keyName)) {
                            is String -> mutablePreferences[stringPreferencesKey(keyName)] =
                                value

                            is Int -> mutablePreferences[intPreferencesKey(keyName)] = value
                            is Boolean -> mutablePreferences[booleanPreferencesKey(keyName)] =
                                value

                            is Double -> mutablePreferences[floatPreferencesKey(keyName)] =
                                value.toFloat()

                            is Long -> mutablePreferences[longPreferencesKey(keyName)] = value
                            is JSONArray -> {
                                val stringSet = mutableSetOf<String>()
                                for (i in 0 until value.length()) {
                                    stringSet.add(value.getString(i))
                                }
                                mutablePreferences[stringSetPreferencesKey(keyName)] = stringSet
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Profile Imported Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Import Failed. File may be corrupted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private val exportLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri != null) {
                exportProfileData(uri)
            }
        }


    private val importLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                importProfileData(uri)
            }
        }
}