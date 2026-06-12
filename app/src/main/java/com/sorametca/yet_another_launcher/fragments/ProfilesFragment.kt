package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import kotlinx.coroutines.launch

class ProfilesFragment : Fragment() {

    private val repository = MainActivity.RepositoryProvider.settingsRepository

    private lateinit var manualExport: TextView
    private lateinit var importProfiles: TextView
    private lateinit var autoExportSwitch: Switch


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

        // set position of status bar toggle
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getAutoExportToggle(requireContext()).collect { state ->
                autoExportSwitch.isChecked = state
            }
        }

        manualExport.setOnClickListener { _: View? ->
            // TODO
            Log.d("option clicked", "manual export")
        }

        importProfiles.setOnClickListener { _: View? ->
            // TODO
            Log.d("option clicked", "import profiles")
        }

        autoExportSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->

            // TODO

            // saving state of toggle
            viewLifecycleOwner.lifecycleScope.launch {
                repository.saveAutoExportToggle(requireContext(), isChecked)
            }
        }
    }
}