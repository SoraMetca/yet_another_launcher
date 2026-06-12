package com.sorametca.yet_another_launcher.model

import com.sorametca.yet_another_launcher.interfaces.LauncherItem
 data class FolderObject(
        val id: String,
        var folderLabel: String = "Folder",
        val apps: ArrayList<AppObject> = ArrayList()
    ) : LauncherItem
