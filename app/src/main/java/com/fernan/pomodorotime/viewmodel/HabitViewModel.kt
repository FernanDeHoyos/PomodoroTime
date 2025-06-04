package com.fernan.pomodorotime.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.fernan.pomodorotime.data.model.Habit

class HabitViewModel : ViewModel() {
    private var nextId = 0
    var habits = mutableStateListOf<Habit>()
        private set

    fun addHabit(title: String, description: String) {
        if (title.isNotBlank()) {
            habits.add(Habit(id = nextId++, title = title, description = description))
        }
    }

    fun toggleHabit(id: Int) {
        habits.replaceAll {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
    }

    fun deleteHabit(id: Int) {
        habits.removeAll { it.id == id }
    }
}
