package com.sorametca.yet_another_launcher.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity
import com.sorametca.yet_another_launcher.activity.SettingsActivity
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.adapter.DragAppData
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.interfaces.LauncherItem
import com.sorametca.yet_another_launcher.model.AppDrawerViewModel
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.AppDrawerStyle
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.interfaces.OnSwipeInterface
import kotlinx.coroutines.launch


class AppDrawerFragment(var appList: ArrayList<LauncherItem?>) : Fragment(), OnSwipeInterface {

    private var backPressedCallback: OnBackPressedCallback? = null

    private val viewModel: AppDrawerViewModel by activityViewModels()
    private lateinit var adapter: AppAdapter
    private lateinit var appChangeReceiver: BroadcastReceiver
    private val repository = MainActivity.RepositoryProvider.settingsRepository


    private lateinit var applyButton: Button
    private lateinit var cancelButton: Button
    private lateinit var mDrawerGridView: GridView
    private lateinit var popupMenuDrawerButton: ImageButton
    private lateinit var searchInput: TextInputEditText
    private lateinit var reorderContainer: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        applyButton = view.findViewById(R.id.apply_button)
        cancelButton = view.findViewById(R.id.cancel_button)
        mDrawerGridView = view.findViewById(R.id.m_grid_view)
        popupMenuDrawerButton = view.findViewById(R.id.popup_menu_drawer_button)
        searchInput = view.findViewById(R.id.search_input)
        reorderContainer = view.findViewById(R.id.reorder_container)

        adapter = AppAdapter(requireContext(), appList)

        searchSetup()
        popupDrawerSetup()
        collapseAppDrawer()
        setupAppChangeReceiver()
        customOrder()

        mDrawerGridView.adapter = adapter
        viewModel.loadInstalledApps(requireContext())

        applyButton.setOnClickListener {
            lifecycleScope.launch {
                repository.saveCustomOrder(
                    viewModel.appList.value
                )

                viewModel.setReorderMode(false)
            }
        }

        cancelButton.setOnClickListener {
            viewModel.cancelReorder()
        }

        lifecycleScope.launch {
            repository.getDrawerStyle(requireContext())
                .collect { style ->
                    adapter.setDrawerStyle(style)
                    if (style == AppDrawerStyle.GRID) mDrawerGridView.numColumns = 4 else mDrawerGridView.numColumns = 1
                    adapter.notifyDataSetChanged()
                }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.appList.collect { apps ->
                adapter.updateList(ArrayList(apps))
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reorderMode.collect { enabled ->

                    adapter.reorderMode = enabled

                    searchInput.visibility = if (enabled) View.GONE else View.VISIBLE
                    popupMenuDrawerButton.visibility = if (enabled) View.GONE else View.VISIBLE
                    reorderContainer.visibility = if (enabled) View.VISIBLE else View.GONE

                    if (enabled) {
                        enableReorderBackHandling()
                    } else {
                        backPressedCallback?.remove()
                        backPressedCallback = null
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.labelsFlow.collect { labels ->
                adapter.labels = labels
                adapter.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hiddenApps.collect { hidden ->
                adapter.hiddenApps = hidden
                adapter.notifyDataSetChanged()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hiddenApps.collect { hidden ->
                adapter.hiddenApps = hidden
                adapter.notifyDataSetChanged()
            }
        }

    }

    // back button when reorder mode is on acts as cancel button
    private fun enableReorderBackHandling() {
        if (backPressedCallback != null) return

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.cancelReorder()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback!!
        )
    }

    private fun customOrder() {

        mDrawerGridView.setOnDragListener { _, event ->
            val mode = viewModel.getReorderMode()
            if (event.action == DragEvent.ACTION_DRAG_STARTED || event.action == DragEvent.ACTION_DROP) {
            }
            if (!mode) return@setOnDragListener false

            when (event.action) {

                DragEvent.ACTION_DROP -> {
                    val dragData = event.localState as? DragAppData ?: return@setOnDragListener true
                    val fromPosition = dragData.originPosition
                        ?: return@setOnDragListener true

                    val targetPosition = mDrawerGridView.pointToPosition(
                        event.x.toInt(),
                        event.y.toInt()
                    )

                    if (targetPosition == AdapterView.INVALID_POSITION) {
                        return@setOnDragListener true
                    }

                    viewModel.moveApp(fromPosition, targetPosition)
                }
            }

            true
        }
    }

    private fun searchSetup() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun popupDrawerSetup() {

        popupMenuDrawerButton.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(), popupMenuDrawerButton)

            popupMenu.menuInflater.inflate(
                R.menu.app_drawer_menu,
                popupMenu.menu
            )

            try {
                val field = PopupMenu::class.java.getDeclaredField("mPopup")
                field.isAccessible = true

                val helper = field.get(popupMenu)

                helper.javaClass
                    .getDeclaredMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    .invoke(helper, true)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.sort_apps -> {
                        parentFragmentManager.beginTransaction()
                            .add(R.id.main, SortAppsFragment())
                            .addToBackStack(null)
                            .commit()
                        true
                    }

                    R.id.main_settings -> {
                        startActivity(
                            Intent(requireContext(), SettingsActivity::class.java)
                        )
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun collapseAppDrawer() { // TODO suppress lint warning, move to OnSwipeListener

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