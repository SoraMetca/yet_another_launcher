package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.model.AppObject

class HiddenApplicationsFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var appAdapter: AppAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hidden_applications, container, false)

        listView = view.findViewById(R.id.hidden_apps_list_view)

        val prefs = requireContext().getSharedPreferences("app_drawer_prefs", Context.MODE_PRIVATE)
        val hiddenSet = prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()

        val pm = requireContext().packageManager
        val hiddenApps = hiddenSet.mapNotNull { packageName ->
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val icon = pm.getApplicationIcon(appInfo)
                AppObject(packageName, label, icon)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        appAdapter = AppAdapter(requireContext(), hiddenApps as ArrayList<AppObject>)
        listView.adapter = appAdapter

        return view
    }
}