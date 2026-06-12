package com.sorametca.yet_another_launcher.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.interfaces.LauncherItem
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import com.sorametca.yet_another_launcher.model.AppObject
import kotlinx.coroutines.launch

class AppDrawerContextMenu(private val appList: ArrayList<LauncherItem?>) : Fragment() {

    private var packageName: String? = null

    private lateinit var adapter: AppAdapter
    private val viewModel: AppDrawerViewModel by activityViewModels()
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var uninstallButton: ImageButton
    private lateinit var renameAppButton: ImageButton
    private lateinit var hideAppButton: ImageButton
    private lateinit var infoButton: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer_context_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uninstallButton = view.findViewById(R.id.uninstall_button)
        renameAppButton = view.findViewById(R.id.rename_button)
        hideAppButton = view.findViewById(R.id.hide_button)
        infoButton = view.findViewById(R.id.info_button)

        setupMenuPositioning(view)
        setupDismissOnOutsideClick(view)

        adapter = AppAdapter(requireContext(), appList)
        settingsRepository = SettingsRepository(requireContext().dataStore, lifecycleScope)

        packageName = arguments?.getString("packageName")

        uninstallButton.setOnClickListener { uninstallApp() }
        renameAppButton.setOnClickListener { renameApp() }
        hideAppButton.setOnClickListener { hideApp() }
        infoButton.setOnClickListener { displayAppInfo() }
    }

    private fun uninstallApp() {
        if (packageName == null) {
            Toast.makeText(requireContext(), "Uninstall unsuccessful", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = "package:$packageName".toUri()
            startActivity(intent)
        }
        parentFragmentManager.popBackStack()
    }

    private fun renameApp() {
        val pkg = packageName ?: return

        val input = EditText(requireContext()).apply {
            hint = "Enter new app name"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val currentLabel = settingsRepository.getLabel(pkg).orEmpty()

            input.setText(currentLabel)
            input.setSelection(currentLabel.length)

            AlertDialog.Builder(requireContext())
                .setTitle("Rename App")
                .setView(input)
                .setPositiveButton("OK") { dialog, _ ->

                    viewLifecycleOwner.lifecycleScope.launch {
                        val newLabel = input.text.toString().trim()

                        if (newLabel.isNotEmpty()) {
                            settingsRepository.setLabel(pkg, newLabel)
                        } else {
                            settingsRepository.removeLabel(pkg)
                        }

                        parentFragmentManager.popBackStack()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun hideApp() {
        val packageName = arguments?.getString("packageName")
        viewModel.hideApp(packageName)
        parentFragmentManager.popBackStack()
    }

    private fun displayAppInfo() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        requireContext().startActivity(intent)
    }

    private fun setupMenuPositioning(view: View) {
        val menu = view.findViewById<LinearLayout>(R.id.menu_container)

        val x = arguments?.getInt("x") ?: 0
        val y = arguments?.getInt("y") ?: 0

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        menu.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val menuWidth = menu.measuredWidth
        val menuHeight = menu.measuredHeight

        var adjustedX = x
        var adjustedY = y + 140

        if (adjustedX + menuWidth > screenWidth) adjustedX = screenWidth - menuWidth - 120
        if (adjustedY + menuHeight > screenHeight) adjustedY = screenHeight - menuHeight - 270

        val params = menu.layoutParams as FrameLayout.LayoutParams
        params.leftMargin = adjustedX
        params.topMargin = adjustedY
        menu.layoutParams = params
    }

    private fun setupDismissOnOutsideClick(view: View) {
        view.findViewById<FrameLayout>(R.id.context_menu_root).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}