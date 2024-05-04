package com.dicoding.asclepius.helper


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.api.ApiConfig
import com.dicoding.asclepius.view.history.HistoryViewModel
import com.dicoding.asclepius.view.news.NewsViewModel
import com.dicoding.asclepius.view.result.ResultViewModel

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val apiService = ApiConfig.getApiService()
            return NewsViewModel(apiService) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(application) as T
        } else if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}