package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.compose.material3.RadioButton
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.R
import android.widget.RadioButton
import com.sorametca.yet_another_launcher.adapter.AppAdapter

class SortAppsFragment : Fragment() {

    lateinit var appList: List<AppObject>
    lateinit var adapter: AppAdapter
    private lateinit var radioGroup : RadioGroup

    companion object {
        fun newInstance(appList: List<AppObject>, adapter: AppAdapter): SortAppsFragment {
            val fragment = SortAppsFragment()
            fragment.appList = appList
            fragment.adapter = adapter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = view.findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            val sortedApps = when (checkedId) {
                R.id.radio_alphabetical_asc ->
                    SortTypes.ALPHABETICAL_ASC.sort(appList)
                R.id.radio_alphabetical_desc ->
                    SortTypes.ALPHABETICAL_DESC.sort(appList)
                R.id.radio_custom ->
                    SortTypes.CUSTOM.sort(appList)

                else -> appList
            }
//            adapter.submitList(sortedApps)
        }
    }
}