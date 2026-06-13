package com.sorametca.yet_another_launcher.view.settings

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.activity.MainActivity

class WallpaperSettingsFragment : Fragment(R.layout.fragment_wallpaper_settings) {

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleSelectedWallpaper(it) }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnPickWallpaper = view.findViewById<Button>(R.id.btnPickWallpaper)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        btnPickWallpaper.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }
    }

    private fun handleSelectedWallpaper(uri: Uri) {
        val activity = requireActivity() as? MainActivity ?: return
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri) ?: ""

        val isGif = mimeType.contains("gif", ignoreCase = true)
        val isVideo = mimeType.contains("video", ignoreCase = true)

        if (isGif) {
            activity.setGifWallpaper(uri)
        } else if (isVideo) {
            activity.setVideoWallpaper(uri)
        } else {
            Toast.makeText(
                requireContext(),
                "Please select a Video or a GIF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}