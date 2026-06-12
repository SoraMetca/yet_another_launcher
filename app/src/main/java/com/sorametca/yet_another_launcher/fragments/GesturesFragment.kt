package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.sorametca.yet_another_launcher.R


class GesturesFragment : Fragment() {

    private val items = listOf("None", "App Drawer", "Home Screen Settings", "Settings")

    private lateinit var swipeToTopSpinner: Spinner
    private lateinit var swipeToBottomSpinner: Spinner
    private lateinit var doubleTapSpinner: Spinner
    private lateinit var longPresspinner: Spinner


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gestures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeToTopSpinner = view.findViewById(R.id.swipe_to_top_spinner)
        swipeToBottomSpinner = view.findViewById(R.id.swipe_to_bottom_spinner)
        doubleTapSpinner = view.findViewById(R.id.double_tap_spinner)
        longPresspinner = view.findViewById(R.id.long_press_spinner)

        val spinners =
            listOf(swipeToTopSpinner, swipeToBottomSpinner, doubleTapSpinner, longPresspinner)

        attachAdapters(spinners)
        itemSelectSetup(spinners)
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
            spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedAction = parent?.getItemAtPosition(position) as String
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }
}