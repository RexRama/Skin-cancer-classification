package com.dicoding.asclepius.view.history

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze
import com.dicoding.asclepius.data.repository.HistoryRepository
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.view.home.MainActivity
import com.dicoding.asclepius.view.news.NewsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var rvHistory: RecyclerView
    private lateinit var viewModel: HistoryViewModel

    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigationView
        bottomNavigation.selectedItemId = R.id.to_history
        setupNavigation(bottomNavigation)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@HistoryActivity, "Press again to exit", Toast.LENGTH_SHORT)
                        .show()
                    @Suppress("DEPRECATION")
                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val viewModelFactory = ViewModelFactory(this@HistoryActivity.application)
        viewModel =
            ViewModelProvider(this@HistoryActivity, viewModelFactory)[HistoryViewModel::class.java]

        rvHistory = binding.rvHistory

        viewModel.getAllHistory().observe(this) { history ->
            if (history.isEmpty()) {
                binding.rvHistory.visibility = View.GONE
                Toast.makeText(this@HistoryActivity, "No History Data", Toast.LENGTH_SHORT).show()
            } else {
                binding.rvHistory.visibility = View.VISIBLE
                showHistoryList(history)
            }
        }


    }

    private fun showHistoryList(history: List<HistoryAnalyze>) {
        rvHistory.layoutManager = LinearLayoutManager(this)
        val historyAdapter = HistoryAdapter(
            history,
            HistoryRepository(this@HistoryActivity.application)
        )
        rvHistory.adapter = historyAdapter
    }

    private fun setupNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.to_home -> {
                    val toHome = Intent(this@HistoryActivity, MainActivity::class.java)
                    startActivity(toHome)
                    return@setOnItemSelectedListener true
                }

                R.id.to_article -> {
                    val toArticle = Intent(this@HistoryActivity, NewsActivity::class.java)
                    startActivity(toArticle)
                    return@setOnItemSelectedListener true
                }

                R.id.to_history -> {
                    false
                }

                else -> false
            }
        }
    }
}