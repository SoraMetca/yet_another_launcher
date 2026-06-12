package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.settings.AppDrawerStyle
import kotlinx.coroutines.launch

class AppDrawerSettingsFragment : Fragment() {

    val options = AppDrawerStyle.entries.map { it.displayName }
    private val repository = MainActivity.RepositoryProvider.settingsRepository

    private lateinit var drawerStyleSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerStyleSpinner = view.findViewById(R.id.drawer_style_spinner)

        viewLifecycleOwner.lifecycleScope.launch {
            repository.getDrawerStyle(requireContext()).collect { style ->
                val position = AppDrawerStyle.entries.indexOf(style)
                drawerStyleSpinner.setSelection(position)
            }
        
        }

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, options
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        drawerStyleSpinner.adapter = adapter

        drawerStyleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val style = AppDrawerStyle.entries[position]

                viewLifecycleOwner.lifecycleScope.launch {
                    repository.saveDrawerStyle(requireContext(), style)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}