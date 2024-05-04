package com.dicoding.asclepius.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.local.room.HistoryDb
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {

    private val historyDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDb.getDatabase(application)
        historyDao = db.historyDao()
    }

    fun getAllHistory(): LiveData<List<HistoryAnalyze>> = historyDao.getAllHistory()

    fun insert(history: HistoryAnalyze) {
        executorService.execute { historyDao.insert(history) }
    }

    fun delete(history: HistoryAnalyze) {
        executorService.execute { historyDao.delete(history) }
    }
}