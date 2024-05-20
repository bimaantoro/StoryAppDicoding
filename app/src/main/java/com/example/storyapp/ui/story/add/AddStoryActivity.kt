package com.example.storyapp.ui.story.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.camera.CameraActivity
import com.example.storyapp.ui.camera.CameraActivity.Companion.CAMERA_RESULT
import com.example.storyapp.ui.camera.CameraActivity.Companion.EXTRA_IMAGE
import com.example.storyapp.ui.story.main.MainStoryActivity
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddStoryActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null


    private val binding: ActivityAddStoryBinding by lazy {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast(getString(R.string.permission_granted))
        } else {
            showToast(getString(R.string.permission_denied))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupAction()
    }

    private fun setupAction() {
        binding.btnOpenGallery.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                showToast(getString(R.string.err_photo_picker_not_available))
                openDocumentPicker()
            }
        }

        binding.btnOpenCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherCamera.launch(intent)
        }

        binding.btnPostStory.setOnClickListener {
            setupPostAction()
        }
    }

    private fun setupPostAction() {
        val description = binding.edtDescription.text.toString()

        if (description.isEmpty()) {
            binding.edtDescription.error = getString(R.string.err_description_field)
            binding.edtDescription.requestFocus()
        } else {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()

                binding.progressBar.visibility = View.VISIBLE

                viewModel.postStory(imageFile, description)

                viewModel.postStoryResult.observe(this) { resultState ->
                    if (resultState != null) {
                        when (resultState) {
                            is ResultState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is ResultState.Error -> {
                                val error = resultState.error
                                showToast(error)
                                binding.progressBar.visibility = View.GONE
                            }

                            is ResultState.Success -> {
                                binding.progressBar.visibility = View.GONE

                                MaterialAlertDialogBuilder(this).apply {
                                    setTitle(getString(R.string.success_title))
                                    setMessage(resultState.data.message)
                                    setPositiveButton(getString(R.string.close_title)) { _, _ ->
                                        moveToMain()
                                    }

                                }
                                    .create()
                                    .show()
                            }
                        }
                    }

                }


            } ?: showToast(getString(R.string.err_image))
        }


    }

    private fun isPhotoPickerAvailable(): Boolean {
        val photoPickerIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val packageManager = packageManager
        return photoPickerIntent.resolveActivity(packageManager) != null
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        launcherOpenDocument.launch(intent)
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPlaceholderStory.setImageURI(it)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_RESULT) {
            currentImageUri = it.data?.getStringExtra(EXTRA_IMAGE)?.toUri()
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast(getString(R.string.err_photo_picker))
        }
    }

    private val launcherOpenDocument = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            if (uri != null) {
                currentImageUri = uri
                showImage()
            }
        } else {
            getString(R.string.err_document_picker)
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainStoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}