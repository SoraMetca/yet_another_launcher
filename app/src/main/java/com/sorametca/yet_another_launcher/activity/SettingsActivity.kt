package com.sorametca.yet_another_launcher.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.fragments.AboutFragment
import com.sorametca.yet_another_launcher.fragments.AppDrawerSettingsFragment
import com.sorametca.yet_another_launcher.fragments.AppearanceFragment
import com.sorametca.yet_another_launcher.fragments.GesturesFragment
import com.sorametca.yet_another_launcher.fragments.HiddenApplicationsFragment
import com.sorametca.yet_another_launcher.fragments.ProfilesFragment

// TODO replace static options with recyclerview

class SettingsActivity : AppCompatActivity() {

    private lateinit var appDrawerOption: LinearLayout
    private lateinit var appearanceOption: LinearLayout
    private lateinit var gesturesOption: LinearLayout
    private lateinit var hiddenApplicationOption : LinearLayout
    private lateinit var profilesOption: LinearLayout
    private lateinit var aboutOption: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        appDrawerOption = findViewById(R.id.app_drawer_option)
        appearanceOption = findViewById(R.id.appearance_option)
        gesturesOption = findViewById(R.id.gestures_option)
        hiddenApplicationOption = findViewById(R.id.hidden_application_option)
        profilesOption = findViewById(R.id.profiles_option)
        aboutOption = findViewById(R.id.about_option)

        val options =
            listOf(appDrawerOption, appearanceOption, gesturesOption, hiddenApplicationOption, profilesOption, aboutOption)

        listenersSetup(options)
    }

    private fun listenersSetup(options: List<LinearLayout>) {
        for (option in options) {
            option.setOnClickListener {
                handleItemClicks(option)
            }
        }
    }

    private fun handleItemClicks(option: LinearLayout) {

        when (option) {
            appDrawerOption -> {
                val fragment = AppDrawerSettingsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_app_drawer_settings")
                    .addToBackStack("fragment_hidden_applications")
                    .commit()
            }

            appearanceOption -> {
                val fragment = AppearanceFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_appearance")
                    .addToBackStack("fragment_hidden_applications")
                    .commit()
            }

            gesturesOption -> {
                val fragment = GesturesFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_gestures")
                    .addToBackStack("fragment_gestures")
                    .commit()
            }

            hiddenApplicationOption -> {
                val fragment = HiddenApplicationsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_hidden_applications")
                    .addToBackStack("fragment_hidden_applications")
                    .commit()
            }

            profilesOption -> {
                val fragment = ProfilesFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_profiles")
                    .addToBackStack("fragment_profiles")
                    .commit()
            }

            aboutOption -> {
                val fragment = AboutFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, fragment, "fragment_about")
                    .addToBackStack("fragment_about")
                    .commit()
            }
        }
    }
}