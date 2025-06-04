package com.fernan.pomodorotime.data.model

data class Habit (
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean=false
)