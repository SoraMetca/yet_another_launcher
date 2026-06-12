package com.sorametca.yet_another_launcher.model

import android.graphics.drawable.Drawable
import com.sorametca.yet_another_launcher.interfaces.LauncherItem

class AppObject(var packageName: String,
                var label: String,
                var icon: Drawable,
                var hidden: Boolean = false
): LauncherItem