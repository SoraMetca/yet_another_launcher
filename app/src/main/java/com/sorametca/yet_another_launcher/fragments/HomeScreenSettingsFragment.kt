package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sorametca.yet_another_launcher.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeScreenSettingsFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_screen_settings_fragment,
            container, false)
    }
}