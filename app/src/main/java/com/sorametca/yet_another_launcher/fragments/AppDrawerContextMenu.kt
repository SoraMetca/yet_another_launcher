package com.sorametca.yet_another_launcher.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.sorametca.yet_another_launcher.R

class AppDrawerContextMenu : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer_context_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        var adjustedY = y + 100

        if (adjustedX + menuWidth > screenWidth) {
            adjustedX = screenWidth - menuWidth - 100
        }
        if (adjustedY + menuHeight > screenHeight) {
            adjustedY = screenHeight - menuHeight - 220
        }

        val params = menu.layoutParams as FrameLayout.LayoutParams
        params.leftMargin = adjustedX
        params.topMargin = adjustedY
        menu.layoutParams = params

        view.findViewById<FrameLayout>(R.id.context_menu_root).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}