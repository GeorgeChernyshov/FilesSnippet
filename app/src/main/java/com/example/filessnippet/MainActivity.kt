package com.example.filessnippet

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.example.filessnippet.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentPhotoPath: String = ""

    private val takePictureCallback = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { successful ->
        if (successful) {
            val photoURI = FileProvider.getUriForFile(
                this,
                "com.example.android.fileprovider",
                File(currentPhotoPath)
            )
            val bitmap = MediaStore.Images.Media.getBitmap(
                contentResolver, photoURI
            )
            val savedImageURL = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                File(currentPhotoPath).name,
                "Image of $title"
            )
            Toast.makeText(this, "Picture Added to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private val selectFromGalleryCallback = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

    }

    private val pickFileCallback = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) {

    }

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

        binding.createInternalAppSpecificButton.setOnClickListener {
            val directory = File(filesDir.path, DIRECTORY_NAME)
            if (!directory.exists())
                directory.mkdir()

            val file = File(directory, String.format(FILE_NAME, (directory.listFiles()?.size ?: 0) + 1))
            file.createNewFile()
            file.writeText(String.format(INTERNAL_FILE_TEXT, file.name))
        }

        binding.getInternalAppSpecificButton.setOnClickListener {
            val directory = File(filesDir.path, DIRECTORY_NAME)
            if (!directory.exists())
                directory.mkdir()

            val file = File(directory, String.format(FILE_NAME, (directory.listFiles()?.size ?: 0)))
            Toast.makeText(this, file.readText(), Toast.LENGTH_SHORT).show()
        }

        binding.createExternalAppSpecificButton.setOnClickListener {
            val directory = File(getExternalFilesDir(null)!!.path, DIRECTORY_NAME)
            if (!directory.exists())
                directory.mkdir()

            val file = File(directory, String.format(FILE_NAME, (directory.listFiles()?.size ?: 0) + 1))
            file.createNewFile()
            file.writeText(String.format(EXTERNAL_FILE_TEXT, file.name))
        }

        binding.getExternalAppSpecificButton.setOnClickListener {
            val directory = File(getExternalFilesDir(null)!!.path, DIRECTORY_NAME)
            if (!directory.exists())
                directory.mkdir()

            val file = File(directory, String.format(FILE_NAME, (directory.listFiles()?.size ?: 0)))
            Toast.makeText(this, file.readText(), Toast.LENGTH_SHORT).show()
        }

        binding.createMediaButton.setOnClickListener {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    it
                )

                takePictureCallback.launch(photoURI)
            }
        }

        binding.getMediaButton.setOnClickListener {
            selectFromGalleryCallback.launch("image/*")
        }

        binding.createSharedButton.setOnClickListener {
            val documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val directory = File(documents, DIRECTORY_NAME)
            if (!directory.exists())
                directory.mkdir()

            val file = File(directory, String.format(FILE_NAME, (directory.listFiles()?.size ?: 0) + 1))
            file.createNewFile()
            file.writeText(String.format(EXTERNAL_FILE_TEXT, file.name))
        }

        binding.getSharedButton.setOnClickListener {
            val documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val directory = File(documents, DIRECTORY_NAME)

            pickFileCallback.launch(Uri.fromFile(directory))
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        const val DIRECTORY_NAME = "directory"
        const val FILE_NAME = "file%d.txt"
        const val INTERNAL_FILE_TEXT = "Internal file by the name %s"
        const val EXTERNAL_FILE_TEXT = "External file by the name %s"
    }
}