package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel

class SortAppsFragment : Fragment() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var viewModel: AppDrawerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AppDrawerViewModel::class.java]
        radioGroup = view.findViewById(R.id.sort_types)

        val savedSortId = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            ?.getInt("sort_type_id", R.id.radio_alphabetical_asc) ?: R.id.radio_alphabetical_asc
        radioGroup.check(savedSortId)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                ?.edit()?.putInt("sort_type_id", checkedId)?.apply()

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
}