package com.sorametca.yet_another_launcher
import android.graphics.drawable.Drawable

class AppInfo {
    var label: CharSequence? = null
    var packageName: CharSequence? = null
    var icon: Drawable? = null
    fun getPackageName(): CharSequence? {
        return packageName
    }
    fun getLabel(): CharSequence? {
        return label
    }
    fun getIcon(): Drawable? {
        return icon
    }
}