package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter

class SortAppsFragment : Fragment() {
    private lateinit var radioGroup : RadioGroup

    interface OnSortSelectedListener {
        fun onSortSelected(sortedList: Int)
    }

    var sortListener: OnSortSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = view.findViewById(R.id.sort_types)

        val savedSortId = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            ?.getInt("sort_type_id", R.id.radio_alphabetical_asc) ?: R.id.radio_alphabetical_asc
        radioGroup.check(savedSortId)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)?.edit()?.putInt("sort_type_id", checkedId)?.apply()
            sortListener?.onSortSelected(checkedId)
        }
    }
}