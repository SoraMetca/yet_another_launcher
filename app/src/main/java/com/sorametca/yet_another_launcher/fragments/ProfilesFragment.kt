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
import com.sorametca.yet_another_launcher.R

// mostly placeholders so far
class ProfilesFragment : Fragment() {

    private lateinit var manualExport: TextView
    private lateinit var importProfiles: TextView
    private lateinit var automaticExportSwitch: Switch


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
        automaticExportSwitch = view.findViewById(R.id.automatic_export_switch)

        manualExport.setOnClickListener { _: View? ->
            // TODO
            Log.d("option clicked", "manual export")
        }

        importProfiles.setOnClickListener { _: View? ->
            // TODO
            Log.d("option clicked", "import profiles")
        }

        automaticExportSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->

            // TODO
            Log.d("option clicked", (if (isChecked) "enabled" else "disabled"))
        }
    }
}