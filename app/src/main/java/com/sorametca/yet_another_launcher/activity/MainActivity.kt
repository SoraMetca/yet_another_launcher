package com.sorametca.yet_another_launcher.activity

import android.app.ActivityOptions
import android.app.Dialog
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.sorametca.yet_another_launcher.GestureAction
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.SettingsRepository
import com.sorametca.yet_another_launcher.activity.MainActivity.RepositoryProvider.settingsRepository
import com.sorametca.yet_another_launcher.adapter.DragAppData
import com.sorametca.yet_another_launcher.adapter.AppAdapter
import com.sorametca.yet_another_launcher.adapter.ViewPagerAdapter
import com.sorametca.yet_another_launcher.dataStore
import com.sorametca.yet_another_launcher.fragments.AppDrawerFragment
import com.sorametca.yet_another_launcher.fragments.HomeScreenSettingsFragment
import com.sorametca.yet_another_launcher.interfaces.LauncherItem
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.model.PagerObject
import com.sorametca.yet_another_launcher.settings.AppDrawerStyle
import com.sorametca.yet_another_launcher.settings.AppLaunchAnim
import com.sorametca.yet_another_launcher.swipes.OnSwipeInterface
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener
import com.sorametca.yet_another_launcher.view.LauncherWidgetView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.sorametca.yet_another_launcher.interfaces.OnSwipeInterface
import com.sorametca.yet_another_launcher.model.FolderObject
import com.sorametca.yet_another_launcher.swipes.OnSwipeListener
import com.sorametca.yet_another_launcher.views.LauncherWidgetView
import com.sorametca.yet_another_launcher.views.AutoFitImageView
import com.sorametca.yet_another_launcher.views.AutoFitVideoView
import java.util.Collections

class MainActivity : AppCompatActivity(), OnSwipeInterface {

    // Class variables
    private val homeAppList = ArrayList<AppObject>()

    private var appLaunchAnim = AppLaunchAnim.FADE_IN

    lateinit var appList: ArrayList<AppObject>

    lateinit var appList: ArrayList<LauncherItem?>
    private lateinit var validator: SettingsRepository
    private var hideStatusBar = false

    // Layout variables
    private lateinit var viewPager: ViewPager
    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    // Helpers
    private lateinit var swipeListener: OnSwipeListener
    private var currentFolderDialog: Dialog? = null

    // Widget Management Components
    private val APPWIDGET_HOST_ID = 1024
    private lateinit var mAppWidgetManager: AppWidgetManager
    private lateinit var mAppWidgetHost: AppWidgetHost

    companion object {
        @Volatile
        var isAppDrawerOpen = false
        @Volatile
        var isDraggingInProgress = false
    }

    private val pickWidgetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1

        if (result.resultCode == RESULT_OK) {
            if (appWidgetId != -1) {
                val appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId)

                if (appWidgetInfo == null) {
                    mAppWidgetHost.deleteAppWidgetId(appWidgetId)
                    return@registerForActivityResult
                }

                if (appWidgetInfo.configure != null) {
                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                        component = appWidgetInfo.configure
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    configureWidgetLauncher.launch(intent)
                } else {
                    createWidget(appWidgetId, appWidgetInfo)
                }
            }
        } else if (result.resultCode == RESULT_CANCELED && appWidgetId != -1) {
            mAppWidgetHost.deleteAppWidgetId(appWidgetId)
        }
    }
    private val configureWidgetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val appWidgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1

        if (result.resultCode == RESULT_OK) {
            if (appWidgetId != -1) {
                val appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId)
                createWidget(appWidgetId, appWidgetInfo)
            }
        } else if (result.resultCode == RESULT_CANCELED && appWidgetId != -1) {
            mAppWidgetHost.deleteAppWidgetId(appWidgetId)
        }
    }

    object RepositoryProvider {
        lateinit var settingsRepository: SettingsRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAppWidgetManager = AppWidgetManager.getInstance(applicationContext)
        mAppWidgetHost = object : AppWidgetHost(applicationContext, APPWIDGET_HOST_ID) {
            override fun onCreateView(
                context: Context,
                appWidgetId: Int,
                appWidgetInfo: AppWidgetProviderInfo?
            ): AppWidgetHostView {
                return LauncherWidgetView(context)
            }
        }

        appList = getInstalledAppList(this)
        initializeHome()
        validator = SettingsRepository(this.dataStore, lifecycleScope)

        val main = findViewById<View>(R.id.main)
        main.isClickable = true
        main.isFocusable = true

        swipeListener = object : OnSwipeListener(this, this) {}

        settingsRepository = SettingsRepository(dataStore, lifecycleScope)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.statusBarToggle.collect { shouldHide ->
                    hideStatusBar = shouldHide

                    val controller =
                        WindowCompat.getInsetsController(window, window.decorView)

                    if (shouldHide) {
                        controller.hide(WindowInsetsCompat.Type.statusBars())
                    } else {
                        controller.show(WindowInsetsCompat.Type.statusBars())
                    }
                }
            }
        }

        main.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    isDraggingInProgress = true
                    true
                }

                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> {
                    isDraggingInProgress = false
                    val dragLocalState = event.localState ?: return@setOnDragListener true

                    val currentWorkspacePage = viewPager.currentItem

                    mViewPagerAdapter.addAppToPage(
                        currentWorkspacePage,
                        dragLocalState,
                        event.x,
                        event.y,
                        v
                    )
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    isDraggingInProgress = false
                    true
                }

                else -> false
            }
        }
    }
    override fun onPause() {
        super.onPause()
        val videoWallpaper = findViewById<AutoFitVideoView>(R.id.videoWallpaper)

        if (videoWallpaper.isPlaying) {
            videoWallpaper.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        val videoWallpaper = findViewById<AutoFitVideoView>(R.id.videoWallpaper)
        videoWallpaper.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        val videoWallpaper = findViewById<AutoFitVideoView>(R.id.videoWallpaper)
        videoWallpaper.stopPlayback()
    }
    fun setGifWallpaper(gifUri: Uri) {
        findViewById<VideoView>(R.id.videoWallpaper).visibility = View.INVISIBLE

        val gifWallpaper = findViewById<AutoFitImageView>(R.id.gifWallpaper)
        gifWallpaper.visibility = View.VISIBLE

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, gifUri)
            val drawable = ImageDecoder.decodeDrawable(source)

            gifWallpaper.setImageDrawable(drawable)

            gifWallpaper.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP

            if (drawable is AnimatedImageDrawable) {
                drawable.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
                drawable.start()
            }
        }
    }
    fun setVideoWallpaper(videoUri: Uri) {
        val videoWallpaper = findViewById<AutoFitVideoView>(R.id.videoWallpaper)

        videoWallpaper.visibility = View.VISIBLE
        videoWallpaper.setVideoURI(videoUri)

        videoWallpaper.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
    }
    fun openFolder(folder: FolderObject) {
        currentFolderDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val bgDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#DD111111"))
                cornerRadius = 48f
            }

            val layout = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                background = bgDrawable
                setPadding(48, 48, 48, 48)
                layoutParams = ViewGroup.LayoutParams(
                    (resources.displayMetrics.widthPixels * 0.85).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val titleView = TextView(this@MainActivity).apply {
                text = folder.folderLabel
                textSize = 20f
                setTextColor(Color.WHITE)
                setPadding(0, 0, 0, 32)
                textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
            }

            val gridView = GridView(this@MainActivity).apply {
                numColumns = 4
                verticalSpacing = 32
            }

            val folderAppsList = ArrayList<LauncherItem?>(folder.apps)
            val adapter = AppAdapter(this@MainActivity, folderAppsList)
            gridView.adapter = adapter

            layout.addView(titleView)
            layout.addView(gridView)
            setContentView(layout)
        }

        currentFolderDialog?.show()
    }

    fun closeFolder() {
        currentFolderDialog?.dismiss()
        currentFolderDialog = null
    }

    override fun onStart() {
        super.onStart()
        mAppWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAppWidgetHost.stopListening()
    }

    fun getInstalledAppList(context: Context): ArrayList<LauncherItem?> {
        val resultList = ArrayList<LauncherItem?>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val packageManager = context.packageManager
        val untreatedAppList: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        for (untreatedApp in untreatedAppList) {
            val localPackageName = untreatedApp.activityInfo.packageName
            val label = untreatedApp.activityInfo.loadLabel(packageManager).toString()
            val icon = untreatedApp.activityInfo.loadIcon(packageManager)
            val app = AppObject(localPackageName, label, icon)
            if (!resultList.contains(app)) {
                resultList.add(app)
            }
        }
        return resultList
    }
    private fun createFolderAtTarget(
        targetPosition: Int,
        targetApp: AppObject,
        draggedApp: AppObject,
        appList: ArrayList<LauncherItem?>
    ) {
        if (targetApp.packageName == draggedApp.packageName) return
        val newFolder = FolderObject(
            id = "folder_${System.currentTimeMillis()}",
            folderLabel = "Folder"
        )
        newFolder.apps.add(targetApp)
        newFolder.apps.add(draggedApp)
        appList[targetPosition] = newFolder
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val appDrawerActive =
            supportFragmentManager.fragments.any { it is AppDrawerFragment && it.isVisible }

        if (appDrawerActive) {
            return super.dispatchTouchEvent(ev)
        }
        swipeListener.onTouch(findViewById(R.id.main), ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun initializeHome() {
        val pagerAppList = ArrayList<PagerObject>()
        val homeAppList = ArrayList<LauncherItem?>(Collections.nCopies(24, null))
        val homeAppList2 = ArrayList<LauncherItem?>(Collections.nCopies(24, null))
        pagerAppList.add(PagerObject(homeAppList))
        pagerAppList.add(PagerObject(homeAppList2))
        viewPager = findViewById(R.id.ViewPager)
        mViewPagerAdapter = ViewPagerAdapter(this, pagerAppList, mAppWidgetHost)
        viewPager.setAdapter(mViewPagerAdapter)
    }

    override fun onLongPress(e: MotionEvent) {
        val appWidgetId = mAppWidgetHost.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        pickWidgetLauncher.launch(pickIntent)
    }

    private fun createWidget(appWidgetId: Int, appWidgetInfo: AppWidgetProviderInfo?) {
        val currentPosition = viewPager.currentItem
        if (::mViewPagerAdapter.isInitialized && currentPosition < mViewPagerAdapter.count) {
            val currentPage = mViewPagerAdapter.pagerAppList[currentPosition]
            currentPage.addWidget(appWidgetId)
            mViewPagerAdapter.addWidgetToPage(currentPosition, appWidgetId, appWidgetInfo)
        }
    }

    fun openHomeScreenSettings() {
        if (supportFragmentManager.findFragmentByTag("home_screen") != null) return
        HomeScreenSettingsFragment().show(supportFragmentManager, "home_screen")
    }

    override fun onDoubleTap() {
        if (validator.gestures["doubleTap"] == GestureAction.openHomeScreenSettings.ordinal) {
            openHomeScreenSettings()
        } else if (validator.gestures["doubleTap"] == GestureAction.openAppList.ordinal) {
            openAppList()
        }
    }

    fun openAppList() {
        isAppDrawerOpen = true
        if (supportFragmentManager.findFragmentByTag("app_drawer") != null) return

        val fragment = AppDrawerFragment(appList)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_out,
                R.anim.slide_in_up,
                R.anim.slide_in_out
            )
            .replace(R.id.main, fragment, "app_drawer")
            .addToBackStack("app_drawer")
            .commit()
    }

    override fun onSwipeToTop() {
        if (validator.gestures["swipeToTop"] == GestureAction.openAppList.ordinal) {
            openAppList()
        } else if (validator.gestures["swipeToTop"] == GestureAction.openHomeScreenSettings.ordinal) {
            openHomeScreenSettings()
        }
    }

    fun itemPress(app: AppObject) {

         lifecycleScope.launch {

             val anim = settingsRepository
                 .getLaunchAnim(this@MainActivity)
                 .first()

             val selectedAnim = when (anim) {
                 AppLaunchAnim.NONE -> R.anim.static_background
                 AppLaunchAnim.FADE_IN -> R.anim.fade_in
                 AppLaunchAnim.ZOOM_IN -> R.anim.zoom_in
                 AppLaunchAnim.SLIDE_BOTTOM -> R.anim.slide_in_bottom
                 AppLaunchAnim.SLIDE_TOP -> R.anim.slide_in_top
                 AppLaunchAnim.ENTER -> R.anim.enter_in
             }

             val options = ActivityOptions.makeCustomAnimation(
                 this@MainActivity,
                 selectedAnim,
                 R.anim.static_background
             )

             val launchAppIntent =
                 applicationContext.packageManager.getLaunchIntentForPackage(app.packageName)
             if (launchAppIntent != null) {
                 applicationContext.startActivity(launchAppIntent, options.toBundle())
             }
         }
    }

    fun collapseFragment() {
        isAppDrawerOpen = false
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && hideStatusBar) {
            WindowCompat.getInsetsController(window, window.decorView)
                .hide(WindowInsetsCompat.Type.statusBars())
        }
    }
}