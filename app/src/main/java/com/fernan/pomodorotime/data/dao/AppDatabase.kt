package com.fernan.pomodorotime.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.data.utils.Converters

@Database(entities = [Habit::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}