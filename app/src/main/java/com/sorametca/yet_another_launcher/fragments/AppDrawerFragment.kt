package com.sorametca.yet_another_launcher.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.DrawerSettings
import com.sorametca.yet_another_launcher.activity.SettingsActivity
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface


class AppDrawerFragment(var appList: ArrayList<AppObject>) : Fragment(), OnSwipeInterface {

    // class variables
    private lateinit var packageName: String

    // helpers
    private lateinit var adapter : AppAdapter
    private lateinit var viewModel: AppDrawerViewModel
    private lateinit var appChangeReceiver: BroadcastReceiver

    // layout variables
    private lateinit var mDrawerGridView: GridView
    private lateinit var searchInput: TextInputEditText
    private lateinit var popupMenuDrawerButton: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        popupMenuDrawerButton = view.findViewById(R.id.popup_menu_drawer_button)
        searchInput = view.findViewById(R.id.search_input)
        mDrawerGridView = view.findViewById(R.id.m_grid_view)

        adapter = AppAdapter(requireContext(), appList)
        mDrawerGridView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[AppDrawerViewModel::class.java]

        viewModel.loadInstalledApps(requireContext())
        viewModel.appList.observe(viewLifecycleOwner) { list ->
            adapter.updateList(ArrayList(list))
        }

        searchSetup()
        popupDrawerSetup()
        collapseAppDrawer()
        setupAppChangeReceiver()
    }

    private fun searchSetup(){
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun popupDrawerSetup(){
        popupMenuDrawerButton.setOnClickListener {
            val popupMenu = PopupMenu(activity?.applicationContext, popupMenuDrawerButton)
            popupMenu.menuInflater.inflate(R.menu.app_drawer_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                if(menuItem.title == "Sort apps"){

                    val fragment = SortAppsFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.add(R.id.main, fragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }

                if(menuItem.title == "App Drawer Settings"){
                    val intent = Intent(activity, DrawerSettings::class.java)
                    startActivity(intent)
                }
                if(menuItem.title == "Launcher Settings"){
                    val intent = Intent(activity, SettingsActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun collapseAppDrawer(){ // TODO suppress lint warning, move to OnSwipeListener
        var startY = 0f

        mDrawerGridView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startY = event.y

                MotionEvent.ACTION_MOVE -> {
                    val diffY = event.y - startY
                    print(diffY)
                    if (diffY > 80 && mDrawerGridView.isAtTop()) {
                        requireActivity().supportFragmentManager.popBackStack()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun GridView.isAtTop(): Boolean {
        return firstVisiblePosition == 0 &&
                (getChildAt(0)?.top ?: 0) >= 0
    }




    private fun setupAppChangeReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        appChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.loadInstalledApps(requireContext())
            }
        }

        requireContext().registerReceiver(appChangeReceiver, filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(appChangeReceiver)
    }
}