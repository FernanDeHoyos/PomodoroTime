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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PomodoroDao = DatabaseProvider.getDatabase(application).pomodoroDao()


    private var timerJob: Job? = null

    private val _time = MutableStateFlow(0)
    val time = _time.asStateFlow()

    private val _millis = MutableStateFlow(0)
    val millis = _millis.asStateFlow()

    private val _totalSessionTime = MutableStateFlow(0)
    val totalSessionTime = _totalSessionTime.asStateFlow()

    val sessionDuration = 1500

    // Este debe ser seteado externamente al seleccionar un hábito
    private var currentHabitId: Int? = null

    fun setHabit(habitId: Int) {
        currentHabitId = habitId
        loadAccumulatedTime()
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
            while (_time.value < sessionDuration) {
                delay(10L)
                _millis.value += 10
                if (_millis.value >= 1000) {
                    _millis.value = 0
                    _time.value += 1
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        _time.value = 0
        _millis.value = 0
    }

    fun saveSession() {
        val seconds = _time.value
        val habitId = currentHabitId ?: return
        if (seconds == 0) return

        viewModelScope.launch {
            dao.insertSession(PomodoroSession(habitId = habitId, totalTimeInSeconds = seconds))
            loadAccumulatedTime()
        }

        resetTimer()
    }
    fun setHabitId(habitId: Int) {
        currentHabitId = habitId
        viewModelScope.launch {
            val total = dao.getTotalAccumulatedTime(habitId) ?: 0
            _totalSessionTime.value = total
        }
    }

}

