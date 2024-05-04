package com.dicoding.asclepius.view.result

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ViewModelFactory

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ResultViewModel

    private val analyzeHistory = HistoryAnalyze()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory(this@ResultActivity.application)
        viewModel =
            ViewModelProvider(this@ResultActivity, viewModelFactory)[ResultViewModel::class.java]

        val result = intent.getStringExtra(EXTRA_RESULT)
        val prediction = intent.getStringExtra(EXTRA_PREDICT)
        val score = intent.getStringExtra(EXTRA_SCORE)
        val data = intent.getStringExtra(EXTRA_IMAGE_URI)
        val imageUri = Uri.parse(data)

        analyzeHistory.image = data
        analyzeHistory.prediction = prediction
        analyzeHistory.score = score

        binding.btnSave.setOnClickListener{
            viewModel.insert(analyzeHistory)
            Toast.makeText(this, "Analyze result added to history", Toast.LENGTH_SHORT).show()
        }

        runOnUiThread {
            imageUri?.let {
                Log.d("Image URI", "showImage: $it")
                binding.resultImage.setImageURI(it)
                binding.resultText.text = result
            }
        }

        Log.d(TAG, "Image URI: $result")
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_PREDICT = "extra_predict"
        const val EXTRA_SCORE = "extra_score"
    }


}