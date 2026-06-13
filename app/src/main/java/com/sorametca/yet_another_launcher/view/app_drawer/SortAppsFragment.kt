package com.sorametca.yet_another_launcher.view.app_drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import kotlinx.coroutines.launch

class SortAppsFragment : Fragment() {

    private var sortId = 0

    private val repository = MainActivity.RepositoryProvider.settingsRepository
    private val viewModel: AppDrawerViewModel by activityViewModels()

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

        lifecycleScope.launch {
            sortId = repository.getSavedSortMethodId(requireContext())
            radioGroup.check(sortId)
        }
    }

    private fun setupRadioListener() {

        radioCustom.setOnClickListener {
            customOrderMode()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            lifecycleScope.launch {
                repository.saveSortMethodId(requireContext(), checkedId)
            }
        }
    }

    private fun customOrderMode() {
        viewModel.setReorderMode(true)
        viewModel.startReorder()

        requireActivity()
            .supportFragmentManager
            .popBackStack()
    }
}