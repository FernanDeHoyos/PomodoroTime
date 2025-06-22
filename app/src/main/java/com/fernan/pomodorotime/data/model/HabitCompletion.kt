package com.fernan.pomodorotime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_completion")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String // formato: yyyy-MM-dd
)