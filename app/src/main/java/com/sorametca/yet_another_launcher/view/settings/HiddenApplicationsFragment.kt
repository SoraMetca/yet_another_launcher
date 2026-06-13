package com.sorametca.yet_another_launcher.view.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.core.adapter.HiddenAppAdapter
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.RepositoryProvider.hiddenAppRepository
import kotlinx.coroutines.launch

class HiddenApplicationsFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var hiddenAppAdapter: HiddenAppAdapter


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

        viewLifecycleOwner.lifecycleScope.launch {
            hiddenAppRepository.hiddenApps.collect { hidden ->
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
                hiddenAppRepository.unhideApp(app.packageName)
            }
        }

        listView.adapter = hiddenAppAdapter
    }
}