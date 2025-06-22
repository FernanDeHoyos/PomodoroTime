package com.fernan.pomodorotime.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("habitId")]
)
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int, // relación con el hábito
    val totalTimeInSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis() // ⬅️ AÑADIR ESTO
)

