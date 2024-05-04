package com.dicoding.asclepius.view.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.api.ApiService
import com.dicoding.asclepius.data.response.ArticlesItem

class NewsViewModel(private val apiService: ApiService) : ViewModel() {
    private val apiKey = BuildConfig.API_KEY
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val newsArticles: LiveData<List<ArticlesItem>> = liveData {
        _loading.value = true
        emit(fetchNewsArticles())
        _loading.value = false
    }

    private suspend fun fetchNewsArticles(): List<ArticlesItem> {
        try {
            val response = apiService.getArticle("cancer", "health", "en", apiKey)
            if (response.status == "ok") {
                return response.articles?.filterNotNull() ?: emptyList()
            } else {
                Log.e("NewsViewModel", "Error fetching articles")
            }
        } catch (e: Exception) {
            Log.e("NewsViewModel", "Error fetching articles", e)
        }
        return emptyList()
    }

}
