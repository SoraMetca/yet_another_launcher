package com.sorametca.yet_another_launcher.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.DrawerSettings
import com.sorametca.yet_another_launcher.activity.SettingsActivity
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface


class AppDrawerFragment : Fragment(), OnSwipeInterface, SortAppsFragment.OnSortSelectedListener {

    private var appList = ArrayList<AppObject>()
    private lateinit var packageName: String
    private lateinit var label: String
    private lateinit var icon: Drawable
    private lateinit var app: AppObject
    private lateinit var popupMenuDrawerButton: ImageButton
    private lateinit var adapter : AppAdapter
    private var startY = 0f
    lateinit var prefs : SharedPreferences

    // layout
    private lateinit var mDrawerGridView: GridView
    private lateinit var searchInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appList = getInstalledAppList()
        popupMenuDrawerButton = view.findViewById(R.id.popup_menu_drawer_button)
        searchInput = view.findViewById(R.id.search_input)
        mDrawerGridView = view.findViewById(R.id.m_grid_view)

        fillGridView()
        applySavedSort()

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        popupMenuDrawerButton.setOnClickListener {
            val popupMenu = PopupMenu(activity?.applicationContext, popupMenuDrawerButton)
            popupMenu.menuInflater.inflate(R.menu.app_drawer_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                if(menuItem.title == "Sort apps"){

                    val fragment = SortAppsFragment()
                    fragment.sortListener = this
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

        val sortFragment = childFragmentManager.findFragmentById(R.id.sort_main) as? SortAppsFragment
        sortFragment?.sortListener = this



    }

    fun fillGridView(){
        adapter = AppAdapter(requireContext(), appList)
        mDrawerGridView.adapter = adapter
    }

    fun GridView.isAtTop(): Boolean {
        return firstVisiblePosition == 0 &&
                (getChildAt(0)?.top ?: 0) >= 0
    }


    fun getInstalledAppList(): ArrayList<AppObject> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val untreatedAppList: List<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(intent, 0)

        for (untreatedApp in untreatedAppList) {
            packageName = untreatedApp.activityInfo.packageName
            label = untreatedApp.activityInfo.loadLabel(requireContext().packageManager).toString()
            icon = untreatedApp.activityInfo.loadIcon(requireContext().packageManager)

            app = AppObject(packageName, label, icon)

            if (!appList.contains(app)) {
                appList.add(app)
            }
        }

        return appList
    }

    override fun onSortSelected(sortedList: Int) {
        val sortedList = when (sortedList) {
            R.id.radio_alphabetical_asc -> SortTypes.ALPHABETICAL_ASC.sort(appList)
            R.id.radio_alphabetical_desc -> SortTypes.ALPHABETICAL_DESC.sort(appList)
            R.id.radio_custom -> SortTypes.CUSTOM.sort(appList)
            else -> appList
        }

        appList.clear()
        appList.addAll(sortedList)
        adapter.updateList(ArrayList(appList))
    }

    private fun applySavedSort() {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedSortId = prefs.getInt("sort_type_id", R.id.radio_alphabetical_asc)

        val sortedList = when(savedSortId) {
            R.id.radio_alphabetical_asc -> SortTypes.ALPHABETICAL_ASC.sort(appList)
            R.id.radio_alphabetical_desc -> SortTypes.ALPHABETICAL_DESC.sort(appList)
            R.id.radio_custom -> SortTypes.CUSTOM.sort(appList)
            else -> appList
        }

        appList.clear()
        appList.addAll(sortedList)
        adapter.updateList(ArrayList(appList))
    }
}