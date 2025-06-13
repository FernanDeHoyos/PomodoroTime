package com.fernan.pomodorotime.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.dao.AppDatabase
import com.fernan.pomodorotime.data.dao.PomodoroDao
import com.fernan.pomodorotime.data.model.PomodoroSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PomodoroDao = DatabaseProvider.getDatabase(application).pomodoroDao()
    private val _remainingTime = MutableStateFlow(0)
    val remainingTime = _remainingTime.asStateFlow()

    private val _pomodorosToday = MutableStateFlow(0)
    val pomodorosToday = _pomodorosToday.asStateFlow()


    private var timerJob: Job? = null

    private val _time = MutableStateFlow(0)
    val time = _time.asStateFlow()

    private val _millis = MutableStateFlow(0)
    val millis = _millis.asStateFlow()

    private val _totalSessionTime = MutableStateFlow(0)
    val totalSessionTime = _totalSessionTime.asStateFlow()

    private val _sessionDuration = MutableStateFlow(0)
    val sessionDuration = _sessionDuration.asStateFlow()


    // Este debe ser seteado externamente al seleccionar un hábito
    private var currentHabitId: Int? = null

    private val _pomodorosPorHabito = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val pomodorosPorHabito: StateFlow<Map<Int, Int>> = _pomodorosPorHabito


    fun getPomodorosForHabitToday(habitId: Int): Int {
        return _pomodorosPorHabito.value[habitId] ?: 0
    }

    private fun loadPomodorosToday() {
        viewModelScope.launch {
            currentHabitId?.let { id ->
                val count = dao.getTodayPomodorosCount(id)
                _pomodorosToday.value = count
            }
        }
    }

    fun setHabit(habitId: Int) {
        currentHabitId = habitId
        loadAccumulatedTime()
        loadPomodorosToday()
    }

    private fun loadAccumulatedTime() {
        viewModelScope.launch {
            currentHabitId?.let { id ->
                val total = dao.getTotalAccumulatedTime(id) ?: 0
                _totalSessionTime.value = total
            }
        }
    }

    fun startTimer() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 || _millis.value > 0) {
                delay(10L)
                _millis.value -= 10
                if (_millis.value < 0) {
                    _millis.value = 990
                    _remainingTime.value -= 1
                    _time.value = _remainingTime.value  // sincroniza
                }
            }
        }
    }


    fun stopTimer() {
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        _remainingTime.value = _sessionDuration.value
        _millis.value = 0
        _time.value = _sessionDuration.value
    }


    fun saveSession(habitId: Int, duration: Int) {
        _totalSessionTime.value += duration
        _pomodorosPorHabito.update { current ->
            val currentCount = current[habitId] ?: 0
            current.toMutableMap().apply {
                this[habitId] = currentCount + 1
            }
        }
    }




    fun setInitialTime(minutes: Int, seconds: Int) {
        stopTimer()
        val total = minutes * 60 + seconds
        _sessionDuration.value = total
        _totalSessionTime.value = total
        _remainingTime.value = total
        _time.value = total
        _millis.value = 0
    }



}

