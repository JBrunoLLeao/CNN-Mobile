package com.andre.tflite.classification

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.andre.tflite.classification.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectedBitmap: Bitmap? = null
    private var selectedModel: String = "fp32"
    private var classifier: Classifier? = null
    private val classes = arrayOf("cat", "dog", "snake")

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            selectedBitmap = bitmap
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission denied to read your storage.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (classifier == null) {
            classifier = Classifier(assets, "model.tflite", 128, 3)
        }

        binding.modelSelector.setOnCheckedChangeListener { _, checkedId ->
            selectedModel = if (checkedId == binding.fp32Model.id) "fp32" else "int8"
            val modelFile = if (selectedModel == "fp32") "model.tflite" else "model_int8.tflite"
            classifier = Classifier(assets, modelFile, 128, 3)
            Toast.makeText(this, "Creating Model: $selectedModel", Toast.LENGTH_SHORT).show()
        }

        binding.btnCamera.setOnClickListener {
            takePictureLauncher.launch(null)
        }

        binding.btnGallery.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.S_V2) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                openGallery()
            }
        }

        binding.btnPredict.setOnClickListener {
            binding.txtResult.text = "Selected model: $selectedModel"
            runPrediction()
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun runPrediction() {
        val bitmap = selectedBitmap ?: run {
            binding.txtResult.text = "Please select an image first"
            return
        }
        val prediction = classifier?.classify(bitmap)?.mapIndexed { index, prob ->
            "${classes.getOrNull(index)}: ${"%.2f".format(java.util.Locale.US, prob * 100)}%"
        }?.sortedByDescending { line ->
            line.substringAfter(": ").substringBefore("%").toDouble()
        }?.joinToString("\n") ?: "Classification failed"

        binding.txtResult.text = "Predicted classes:\n$prediction"

    }

    override fun onStop() {
        super.onStop()
    }
}