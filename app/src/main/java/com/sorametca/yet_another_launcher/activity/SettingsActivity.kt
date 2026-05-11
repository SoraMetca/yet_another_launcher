package com.sorametca.yet_another_launcher.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.fragments.HiddenApplicationsFragment


class SettingsActivity : AppCompatActivity() {

    private val options = listOf("Home Screen", "Global Appearance", "Gestures", "Hidden Applications", "Import/Export Profiles", "About")
    private lateinit var listView : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        listView = findViewById(R.id.hidden_apps_list_view)

        val arr: ArrayAdapter<String> = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,options)

        listView.adapter = arr

        handleItemClicks()
    }

    private fun handleItemClicks(){
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedOption = options[position]

            when (selectedOption) {
                "Home Screen" -> {
                    // TODO handle Home Screen click
                }
                "Global Appearance" -> {
                    // TODO handle Global Appearance click
                }
                "Gestures" -> {
                    // TODO handle Gestures click
                }
                "Hidden Applications" -> {
                    val fragment = HiddenApplicationsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fragment, "fragment_hidden_applications")
                        .addToBackStack("fragment_hidden_applications")
                        .commit()
                }
                "Import/Export Profiles" -> {
                    // TODO handle Import/Export Profiles click
                }
                "About" -> {
                    // TODO handle About click
                }
            }
        }
    }
}