package com.fernan.pomodorotime.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.data.model.HabitCompletion
import com.fernan.pomodorotime.data.model.PomodoroSession
import com.fernan.pomodorotime.utils.Converters

@Database(
    entities = [Habit::class, PomodoroSession::class, HabitCompletion::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}
