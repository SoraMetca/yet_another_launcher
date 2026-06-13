package com.sorametca.yet_another_launcher.model

data class FolderObject(
        val id: String,
        var folderLabel: String = "Folder",
        val apps: ArrayList<AppObject> = ArrayList()
    ) : LauncherItem
