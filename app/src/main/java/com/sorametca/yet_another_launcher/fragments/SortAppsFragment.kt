package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel

class SortAppsFragment : Fragment() {

    // helpers
    private lateinit var viewModel: AppDrawerViewModel

    // layout variables
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

        viewModel = ViewModelProvider(requireActivity())[AppDrawerViewModel::class.java]

        val savedSortId = getSavedSortMethodId()

        radioGroup.check(savedSortId)

        setupRadioListener()
    }

    private fun setupRadioListener() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            saveSortMethodId(checkedId)

            val sortedList = when (checkedId) {
                R.id.radio_alphabetical_asc -> viewModel.appList.value?.sortedBy { it.label }
                R.id.radio_alphabetical_desc -> viewModel.appList.value?.sortedByDescending { it.label }
                R.id.radio_custom -> viewModel.appList.value
                else -> viewModel.appList.value
            } ?: emptyList()

            viewModel.updateAppList(sortedList)
            viewModel.setSortType(checkedId)
        }
    }

    private fun getSavedSortMethodId(): Int {
        return context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            ?.getInt("sort_type_id", R.id.radio_alphabetical_asc)
            ?: R.id.radio_alphabetical_asc
    }

    private fun saveSortMethodId(checkedId: Int) {
        context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)?.edit {
            putInt("sort_type_id", checkedId)
        }
    }
}