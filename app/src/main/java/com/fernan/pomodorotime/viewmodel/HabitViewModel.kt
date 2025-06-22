package com.fernan.pomodorotime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.data.model.HabitCompletion
import com.fernan.pomodorotime.utils.scheduleHabitReminder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val habitDao = DatabaseProvider.getDatabase(application).habitDao()

    private val _completedHabitsToday = MutableStateFlow<Set<Int>>(emptySet())
    val completedHabitsToday: StateFlow<Set<Int>> = _completedHabitsToday.asStateFlow()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    init {
        viewModelScope.launch {
            refreshCompletedHabitsToday()
            habitDao.getAllHabits()
                .flowOn(Dispatchers.IO)
                .collect { habitList ->
                    _habits.value = habitList
                }
        }
    }

    private fun refreshCompletedHabitsToday() {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = DatabaseProvider.getDatabase(getApplication()).habitCompletionDao()
            val today = getCurrentDate()
            val completions = dao.getCompletionsForDate(today)
            _completedHabitsToday.emit(completions.map { it.habitId }.toSet())
        }
    }



    fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    fun toggleHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = getCurrentDate()
            val dao = DatabaseProvider.getDatabase(getApplication()).habitCompletionDao()
            val alreadyCompleted = dao.isHabitCompletedToday(id, date)

            if (alreadyCompleted == 0) {
                dao.insertCompletion(HabitCompletion(habitId = id, date = date))
                refreshCompletedHabitsToday()
            }
        }
    }

    fun markHabitAsCompletedByPomodoro(habitId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = DatabaseProvider.getDatabase(getApplication()).habitCompletionDao()
            val date = getCurrentDate()
            val alreadyCompleted = dao.isHabitCompletedToday(habitId, date)

            if (alreadyCompleted == 0) {
                dao.insertCompletion(HabitCompletion(habitId = habitId, date = date))
                refreshCompletedHabitsToday()
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

            val habitId = habitDao.insertHabit(newHabit).toInt()
            val savedHabit = newHabit.copy(id = habitId)

            reminderTime?.let { time ->
                if (time.matches(Regex("""\d{2}:\d{2}"""))) {
                    scheduleHabitReminder(
                        getApplication<Application>().applicationContext,
                        savedHabit.id.toLong(),
                        savedHabit.title,
                        time
                    )
                }
            }

        }
    }



    fun updateHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            habitDao.updateHabit(habit)

            habit.reminderTime?.let { time ->
                if (time.matches(Regex("""\d{2}:\d{2}"""))) {
                    scheduleHabitReminder(
                        getApplication<Application>().applicationContext,
                        habit.id.toLong(),
                        habit.title,
                        time
                    )
                }
            }
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
