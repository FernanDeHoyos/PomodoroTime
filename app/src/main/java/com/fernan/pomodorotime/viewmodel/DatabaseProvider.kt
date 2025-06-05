package com.fernan.pomodorotime.viewmodel

import android.content.Context
import androidx.room.Room
import com.fernan.pomodorotime.data.dao.AppDatabase

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pomodoro-db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}