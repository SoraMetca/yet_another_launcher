package com.sorametca.yet_another_launcher.fragments

import android.app.AlertDialog
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import androidx.core.content.edit
import com.sorametca.yet_another_launcher.activity.MainActivity

class AppDrawerContextMenu : Fragment() {

    // class variables
    private var packageName: String? = null

    // helpers
    private lateinit var viewModel: AppDrawerViewModel

    // layout variables
    private lateinit var uninstallButton: ImageButton
    private lateinit var renameApp : ImageButton
    private lateinit var hideApp : ImageButton
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
        uninstallButton.setOnClickListener { uninstallApp() }

        renameApp = view.findViewById(R.id.rename_button)
        renameApp.setOnClickListener { renameApp() }

        hideApp = view.findViewById(R.id.hide_button)
        hideAppSetup()


        infoButton = view.findViewById(R.id.info_button)
        infoButton.setOnClickListener { displayAppInfo() }


        viewModel = ViewModelProvider(requireActivity())[AppDrawerViewModel::class.java]
        packageName = arguments?.getString("packageName")

        setupMenuPositioning(view)
        setupDismissOnOutsideClick(view)
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
        if (packageName == null) return

        val input = EditText(requireContext()).apply {
            hint = "Enter new app name"
            val prefs = requireContext().getSharedPreferences("app_drawer_prefs", Context.MODE_PRIVATE)
            setText(prefs.getString(packageName, "") ?: "")
            setSelection(text.length)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Rename App")
            .setView(input)
            .setPositiveButton("OK") { dialog, _ ->
                val newLabel = input.text.toString().trim()
                if (newLabel.isNotEmpty()) {
                    val prefs = requireContext().getSharedPreferences("app_drawer_prefs", Context.MODE_PRIVATE)
                    prefs.edit { putString(packageName!!, newLabel) }
                }
                dialog.dismiss()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun hideAppSetup() {
        hideApp.setOnClickListener {
            val packageName = arguments?.getString("packageName") ?: return@setOnClickListener
            viewModel.hideApp(requireContext(), packageName)
            parentFragmentManager.popBackStack()
        }
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
        var adjustedY = y

        if (adjustedX + menuWidth > screenWidth) adjustedX = screenWidth - menuWidth - 120
        if (adjustedY + menuHeight > screenHeight) adjustedY = screenHeight - menuHeight - 220

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