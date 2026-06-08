package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
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
    private lateinit var radioCustom : RadioButton

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

        viewModel = ViewModelProvider(requireActivity())[AppDrawerViewModel::class.java]

        val savedSortId = getSavedSortMethodId()

        radioGroup.check(savedSortId)

        setupRadioListener()
    }

    private fun setupRadioListener() {

        radioCustom.setOnClickListener {
            customOrderMode()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            saveSortMethodId(checkedId)

            val sortedList = when (checkedId) {
                R.id.radio_alphabetical_asc ->
                    viewModel.appList.value?.sortedBy { it.label }

                R.id.radio_alphabetical_desc ->
                    viewModel.appList.value?.sortedByDescending { it.label }

                R.id.radio_custom -> { null }

                else -> viewModel.appList.value
            }

            sortedList?.let {
                viewModel.updateAppList(it)
            }
            viewModel.setSortType(checkedId)
        }
    }

    private fun customOrderMode() {
        viewModel.setReorderMode(true)
        viewModel.startReorder()
        requireActivity().supportFragmentManager.popBackStack()
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