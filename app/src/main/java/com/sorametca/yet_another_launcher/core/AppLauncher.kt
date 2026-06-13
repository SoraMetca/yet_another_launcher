package com.sorametca.yet_another_launcher.core

import android.app.ActivityOptions
import android.content.Context
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.model.AppObject
import com.sorametca.yet_another_launcher.settings.RepositoryProvider.appLaunchAnimRepository
import com.sorametca.yet_another_launcher.settings.options.AppLaunchAnim
import kotlinx.coroutines.flow.first

class AppLauncher(private val context: Context, ) {

    suspend fun launch(app: AppObject) {

        val anim = appLaunchAnimRepository
            .getLaunchAnim(context)
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
            context,
            selectedAnim,
            R.anim.static_background
        )

        context.packageManager
            .getLaunchIntentForPackage(app.packageName)
            ?.let {
                context.startActivity(it, options.toBundle())
            }
    }
}