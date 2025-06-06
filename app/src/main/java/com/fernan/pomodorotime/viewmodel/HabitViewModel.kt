package com.fernan.pomodorotime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.model.Habit
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val habitDao = DatabaseProvider.getDatabase(application).habitDao()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        viewModelScope.launch {
            habitDao.getAllHabits()
                .flowOn(Dispatchers.IO)
                .collect { habitList ->
                    _habits.value = habitList
                }
        }
    }

    fun addHabit(
        title: String,
        description: String,
        reminderTime: String? = null,
        activeDays: List<Int> = emptyList()
    ) {
        if (title.isBlank()) return
        val newHabit = Habit(
            title = title,
            description = description,
            reminderTime = reminderTime,
            activeDays = activeDays
        )
        viewModelScope.launch(Dispatchers.IO) {
            habitDao.insertHabit(newHabit)
        }
    }

    fun toggleHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = _habits.value.find { it.id == id }
            habit?.let {
                val updated = it.copy(isDone = !it.isDone)
                habitDao.updateHabit(updated)
            }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            habitDao.updateHabit(habit)
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = _habits.value.find { it.id == id }
            habit?.let {
                habitDao.deleteHabit(it)
            }
        }
    }
}
