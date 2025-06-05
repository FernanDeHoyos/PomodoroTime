package com.fernan.pomodorotime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val habitDao = DatabaseProvider.getDatabase(application).habitDao()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            val habitsFromDb = habitDao.getAllHabits()
            _habits.value = habitsFromDb
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
            loadHabits()
        }
    }


    fun toggleHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = habitDao.getAllHabits().find { it.id == id }
            habit?.let {
                val updated = it.copy(isDone = !it.isDone)
                habitDao.updateHabit(updated)
                loadHabits()
            }
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = habitDao.getAllHabits().find { it.id == id }
            habit?.let {
                habitDao.deleteHabit(it)
                loadHabits()
            }
        }
    }
}