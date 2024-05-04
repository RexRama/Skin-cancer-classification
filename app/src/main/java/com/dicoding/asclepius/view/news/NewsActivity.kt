package com.dicoding.asclepius.view.news

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
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.home.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("OverrideDeprecatedMigration")
class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var rvNews: RecyclerView
    private lateinit var viewModel: NewsViewModel

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigationView
        bottomNavigation.selectedItemId = R.id.to_article
        setupNavigation(bottomNavigation)

        val viewModelFactory = ViewModelFactory(this@NewsActivity.application)
        rvNews = binding.rvArticle

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@NewsActivity, "Press again to exit", Toast.LENGTH_SHORT)
                        .show()
                    @Suppress("DEPRECATION")
                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel =
            ViewModelProvider(this@NewsActivity, viewModelFactory)[NewsViewModel::class.java]

        viewModel.newsArticles.observe(this@NewsActivity) { newsArticles ->
            if (newsArticles.isNotEmpty()) {
                showRecyclerList(newsArticles)
            } else {
                handleEmptyList()
            }
        }

        viewModel.loading.observe(this) {
            showLoading(it)
        }


    }


    private fun setupNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.to_home -> {
                    val toHome = Intent(this@NewsActivity, MainActivity::class.java)
                    startActivity(toHome)
                    return@setOnItemSelectedListener true
                }

                R.id.to_article -> {
                    false
                }

                R.id.to_history -> {
                    val toHistory = Intent(this@NewsActivity, HistoryActivity::class.java)
                    startActivity(toHistory)
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun handleEmptyList() {
        Toast.makeText(this, "Empty List", Toast.LENGTH_SHORT).show()
    }

    private fun showRecyclerList(newsList: List<ArticlesItem>) {
        rvNews.layoutManager = LinearLayoutManager(this)
        val newsAdapter = NewsAdapter(newsList)
        rvNews.adapter = newsAdapter
        newsAdapter.updateData(newsList)

    }
}