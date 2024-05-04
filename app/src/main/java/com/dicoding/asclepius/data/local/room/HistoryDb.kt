package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.HistoryAnalyze

@Database(entities = [HistoryAnalyze::class], version = 1)
abstract class HistoryDb: RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDb? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoryDb {
            if (INSTANCE == null) {
                synchronized(HistoryDb::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HistoryDb::class.java, "history_db")
                        .build()
                }
            }
            return INSTANCE as HistoryDb
        }
    }
}