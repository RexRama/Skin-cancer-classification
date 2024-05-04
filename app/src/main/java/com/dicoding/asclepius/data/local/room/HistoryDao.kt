package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: HistoryAnalyze)

    @Update
    fun update(history: HistoryAnalyze)

    @Delete
    fun delete(history: HistoryAnalyze)

    @Query("SELECT  * from HistoryAnalyze")
    fun getAllHistory(): LiveData<List<HistoryAnalyze>>
}