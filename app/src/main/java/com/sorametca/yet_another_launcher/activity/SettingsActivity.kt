package com.sorametca.yet_another_launcher.activity

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.SettingsAdapter
import com.sorametca.yet_another_launcher.fragments.AboutFragment
import com.sorametca.yet_another_launcher.fragments.AppDrawerSettingsFragment
import com.sorametca.yet_another_launcher.fragments.AppearanceFragment
import com.sorametca.yet_another_launcher.fragments.HiddenApplicationsFragment
import com.sorametca.yet_another_launcher.fragments.ProfilesFragment


class SettingsActivity : AppCompatActivity() {

    var icons = listOf(
        R.drawable.home,
        R.drawable.global_appearance,
        R.drawable.gestures,
        R.drawable.hide_settings,
        R.drawable.profiles,
        R.drawable.about
    )
    private val titles = listOf(
        "App Drawer",
        "Global Appearance",
        "Gestures",
        "Hidden Applications",
        "Import/Export Profiles",
        "About"
    )
    private lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        listView = findViewById(R.id.hidden_apps_list_view)

        listView.adapter = SettingsAdapter(
            this,
            titles,
            icons
        )

        handleItemClicks()
    }

    private fun handleItemClicks() {
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedOption = titles[position]

            when (selectedOption) {
                "App Drawer" -> { // TODO make it not dependent on titles
                    val fragment = AppDrawerSettingsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_app_drawer_settings")
                        .addToBackStack("fragment_hidden_applications")
                        .commit()
                }

                "Global Appearance" -> {
                    val fragment = AppearanceFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_appearance")
                        .addToBackStack("fragment_hidden_applications")
                        .commit()
                }

                "Gestures" -> {
                    val fragment = AppearanceFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_gestures")
                        .addToBackStack("fragment_gestures")
                        .commit()
                }

                "Hidden Applications" -> {
                    val fragment = HiddenApplicationsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_hidden_applications")
                        .addToBackStack("fragment_hidden_applications")
                        .commit()
                }

                "Import/Export Profiles" -> {
                    val fragment = ProfilesFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_profiles")
                        .addToBackStack("fragment_profiles")
                        .commit()
                }

                "About" -> {
                    val fragment = AboutFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_about")
                        .addToBackStack("fragment_about")
                        .commit()
                }
            }
        }
    }
}