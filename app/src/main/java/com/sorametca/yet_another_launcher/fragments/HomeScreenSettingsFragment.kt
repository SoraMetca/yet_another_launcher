package com.sorametca.yet_another_launcher.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import com.sorametca.yet_another_launcher.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorametca.yet_another_launcher.activity.SettingsActivity

class HomeScreenSettingsFragment : BottomSheetDialogFragment() {

    private lateinit var wallpaperOption : LinearLayout
    private lateinit var widgetsOption : LinearLayout
    private lateinit var addFolder : LinearLayout
    private lateinit var settingsButton : ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_screen_settings_fragment,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wallpaperOption = view.findViewById(R.id.wallpaper_option)
        wallpaperOption.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.settings_container, WallpaperSettingsFragment())
                .addToBackStack("wallpaper_settings")
                .commit()
        }
        widgetsOption = view.findViewById(R.id.widgets_option)
        addFolder = view.findViewById(R.id.add_folder_option)
        settingsButton = view.findViewById(R.id.settings_button)
        widgetsOption.setOnClickListener { /* TODO */ }
        addFolder.setOnClickListener { /* TODO */ }
        settingsButton.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }
}