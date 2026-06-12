package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.settings.AppLaunchAnim
import kotlinx.coroutines.launch

class AppearanceFragment : Fragment() {

    val options = AppLaunchAnim.entries.map { it.displayName }

    private val repository = MainActivity.RepositoryProvider.settingsRepository

    private lateinit var appAnimSpinner : Spinner
    private lateinit var statusBarSwitch : Switch


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appearance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appAnimSpinner = view.findViewById(R.id.drawer_style_spinner)
        statusBarSwitch = view.findViewById(R.id.status_bar_switch)

        // set position of spinner on launch
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getLaunchAnim(requireContext()).collect { anim ->
                val position = AppLaunchAnim.entries.indexOf(anim)
                appAnimSpinner.setSelection(position)
            }
        }

        // set position of status bar toggle
        viewLifecycleOwner.lifecycleScope.launch {
            repository.getStatusBarToggle(requireContext()).collect { state ->
                statusBarSwitch.isChecked = state
            }
        }

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, options
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val controller = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )

        appAnimSpinner.adapter = adapter

        appAnimSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val anim = AppLaunchAnim.entries[position]

                viewLifecycleOwner.lifecycleScope.launch {
                    repository.saveLaunchAnim(requireContext(), anim)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        statusBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                repository.saveStatusBarToggle(requireContext(), isChecked)
            }
        }
    }
}