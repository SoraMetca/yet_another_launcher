package com.sorametca.yet_another_launcher.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import com.sorametca.yet_another_launcher.adapter.HiddenAppAdapter
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import com.sorametca.yet_another_launcher.model.AppObject
import kotlinx.coroutines.launch

class HiddenApplicationsFragment : Fragment() {

    private lateinit var hiddenAppAdapter: HiddenAppAdapter
    private lateinit var settingsRepository: SettingsRepository
    private val viewModel: AppDrawerViewModel by activityViewModels()

    private lateinit var listView: ListView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hidden_applications, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.hidden_apps_list_view)

        settingsRepository = SettingsRepository(requireContext().dataStore, lifecycleScope)


        viewLifecycleOwner.lifecycleScope.launch {
            settingsRepository.hiddenApps.collect { hidden ->
                updateHiddenList(hidden)
            }
        }
    }

    private fun updateHiddenList(hiddenSet: Set<String>) {

        val pm = requireContext().packageManager

        val hiddenApps = hiddenSet.mapNotNull { packageName ->
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val icon = pm.getApplicationIcon(appInfo)
                AppObject(packageName, label, icon)
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        }

        hiddenAppAdapter = HiddenAppAdapter(
            requireContext(),
            ArrayList(hiddenApps)
        ) { app ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsRepository.unhideApp(app.packageName)
            }
        }

        listView.adapter = hiddenAppAdapter
    }
}