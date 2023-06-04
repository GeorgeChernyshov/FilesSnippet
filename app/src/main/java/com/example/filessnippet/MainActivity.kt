package com.example.filessnippet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.view.isVisible
import com.example.filessnippet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.externalDriveStateTextView.text = getString(when (Environment.getExternalStorageState()) {
            Environment.MEDIA_BAD_REMOVAL -> R.string.external_storage_bad_removal
            Environment.MEDIA_CHECKING -> R.string.external_storage_checking
            Environment.MEDIA_EJECTING -> R.string.external_storage_ejecting
            Environment.MEDIA_MOUNTED -> R.string.external_storage_mounted
            Environment.MEDIA_MOUNTED_READ_ONLY -> R.string.external_storage_mounted_read_only
            Environment.MEDIA_NOFS -> R.string.external_storage_nofs
            Environment.MEDIA_REMOVED -> R.string.external_storage_removed
            Environment.MEDIA_SHARED -> R.string.external_storage_shared
            Environment.MEDIA_UNMOUNTABLE -> R.string.external_storage_unmountable
            Environment.MEDIA_UNMOUNTED -> R.string.external_storage_unmounted
            else -> R.string.external_storage_unknown
        })

        binding.externalAppSpecificLayout.isVisible = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}