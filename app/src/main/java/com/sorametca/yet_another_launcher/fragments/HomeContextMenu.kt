package com.sorametca.yet_another_launcher.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sorametca.yet_another_launcher.R

class HomeContextMenu(private var packageName : String) : Fragment() {
    private lateinit var removeShortcutButton : LinearLayout
    private lateinit var appInfoButton : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_context_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        removeShortcutButton = view.findViewById(R.id.item_remove_shortcut)
        appInfoButton = view.findViewById(R.id.item_app_info)

        setupMenuPositioning(view)
        setupDismissOnOutsideClick(view)

        removeShortcutButton.setOnClickListener { removeShortcut(packageName) }
        appInfoButton.setOnClickListener { appInfoButton(packageName) }
    }

    private fun removeShortcut(packageName : String){

    }

    private fun appInfoButton(packageName : String){ // TODO remove dupe functions from AppDrawerContextMenu
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        requireContext().startActivity(intent)
    }

    private fun setupMenuPositioning(view: View) {
        val menu = view.findViewById<LinearLayout>(R.id.home_context_menu_container)

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