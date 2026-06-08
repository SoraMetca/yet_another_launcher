package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SortAppsFragment : Fragment() {

    private var sortId = 0

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: AppDrawerViewModel

    private lateinit var radioCustom: RadioButton
    private lateinit var radioGroup: RadioGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = view.findViewById(R.id.sort_types)
        radioCustom = view.findViewById(R.id.radio_custom)

        setupRadioListener()

        settingsRepository = SettingsRepository(requireContext().dataStore, lifecycleScope)
        viewModel = AppDrawerViewModel(settingsRepository)

        lifecycleScope.launch {
            sortId = settingsRepository.getSavedSortMethodId(requireContext())
            radioGroup.check(sortId)
        }
    }

    private fun setupRadioListener() {

        radioCustom.setOnClickListener {
            customOrderMode()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            lifecycleScope.launch {
                settingsRepository.saveSortMethodId(requireContext(), sortId)
            }

            val sortedList = when (checkedId) {
                R.id.radio_alphabetical_asc -> viewModel.appList.value.sortedBy { it.label }

                R.id.radio_alphabetical_desc -> viewModel.appList.value.sortedByDescending { it.label }

                R.id.radio_custom -> null

                else -> viewModel.appList.value
            }

            sortedList?.let {
                viewModel.updateAppList(it)
            }
            viewModel.setSortType(checkedId)
        }
    }

    private fun customOrderMode() {
        viewModel.reorderMode
            .drop(1)
            .onEach { enabled ->
                if (enabled) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}