package com.dicoding.asclepius.view.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.history.HistoryViewModel
import com.dicoding.asclepius.view.news.NewsActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var viewModel: HistoryViewModel

    private var doubleBackToExitPressedOnce = false
    private var currentImageUri: Uri? = null
    private var results: String? = null
    private var prediction: String? = null
    private var score: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigationView
        bottomNavigation.selectedItemId = R.id.to_home
        setupNavigation(bottomNavigation)

        val viewModelFactory = ViewModelFactory(this@MainActivity.application)
        viewModel =
            ViewModelProvider(this@MainActivity, viewModelFactory)[HistoryViewModel::class.java]

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@MainActivity, "Press again to exit", Toast.LENGTH_SHORT)
                        .show()
                    @Suppress("DEPRECATION")
                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val intent = Intent(this, ResultActivity::class.java)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.analyzeButton.setOnClickListener {
            analyzeImage(intent)
            moveToResult(intent)
        }
    }


    private fun setupNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.to_home -> {
                    false
                }

                R.id.to_article -> {
                    val toArticle = Intent(this@MainActivity, NewsActivity::class.java)
                    startActivity(toArticle)
                    return@setOnItemSelectedListener true
                }

                R.id.to_history -> {
                    val toHistory = Intent(this@MainActivity, HistoryActivity::class.java)
                    startActivity(toHistory)
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            startUCrop(uri)
        } else {
            Log.d("Photo Picker", "No media Selected")
        }
    }

    private fun startUCrop(uri: Uri) {
        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.teal))
            setActiveControlsWidgetColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.teal_dark
                )
            )
            setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.teal))
            setToolbarWidgetColor(Color.WHITE)
        }

        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped_image")))
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        uCrop.start(this)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let {
                Log.e("UCrop", "Error cropping image: $cropError")
            }
        }
    }

    private fun analyzeImage(uri: Intent) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResults(result: List<Classifications>?, inferenceTime: Long) {
                    result?.let { it ->
                        if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                            println(it)
                            val sortedCategories =
                                it[0].categories.sortedByDescending { it?.score }
                            results =
                                sortedCategories.joinToString("\n") {
                                    "${it.label} " + NumberFormat.getPercentInstance()
                                        .format(it.score).trim()
                                }
                            prediction = sortedCategories[0].label
                            score =
                                NumberFormat.getPercentInstance().format(sortedCategories[0].score)
                        } else {
                            showToast()
                        }
                    }
                }

                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        currentImageUri?.let { this.imageClassifierHelper.classifyStaticImage(it) }
        uri.putExtra(ResultActivity.EXTRA_RESULT, results)
        uri.putExtra(ResultActivity.EXTRA_PREDICT, prediction)
        uri.putExtra(ResultActivity.EXTRA_SCORE, score)
    }


    private fun moveToResult(intent: Intent) {
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String = "") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}