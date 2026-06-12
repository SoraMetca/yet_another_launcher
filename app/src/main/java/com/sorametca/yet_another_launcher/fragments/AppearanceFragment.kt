package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
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
import com.sorametca.yet_another_launcher.R
class AppearanceFragment : Fragment() {

    private var appLaunchAnim = listOf("None", "Fade in", "Zoom in","Slide in from bottom", "Slide in from top", "Enter")
    private var temporary: Int? = null

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

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, appLaunchAnim
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
                val selectedItem = parent.getItemAtPosition(position).toString()

                when(selectedItem){
                    "Fade in" -> temporary = R.anim.fade_in
                    "Zoom in" -> temporary = R.anim.fade_in
                    "Slide in from bottom" -> R.anim.slide_in_up
                    "Slide in from top" ->  R.anim.slide_in_top
                    "Enter" -> R.anim.enter_in
                    else -> temporary = null
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
        }
    }
}