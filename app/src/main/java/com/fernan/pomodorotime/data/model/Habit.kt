package com.fernan.pomodorotime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // 0 indica que Room autogenerará
    val title: String,
    val description: String,
    var isDone: Boolean = false,
    val activeDays: List<Int> = emptyList(), // O usa un TypeConverter si Room lo requiere
    val reminderTime: String? = null,        // Nuevo campo opcional para la hora
    val createdDate: Long = System.currentTimeMillis() // Opcional si lo usas
)