package com.fernan.pomodorotime.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.dao.AppDatabase
import com.fernan.pomodorotime.data.dao.PomodoroDao
import com.fernan.pomodorotime.data.model.Habit
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

    private val _tiempoPorHabito = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val tiempoPorHabito: StateFlow<Map<Int, Int>> = _tiempoPorHabito


    fun getPomodorosForHabitToday(habitId: Int): Int {
        return _pomodorosPorHabito.value[habitId] ?: 0
    }


    fun setHabit(habitId: Int) {
        currentHabitId = habitId
        viewModelScope.launch {
            val count = dao.getTodayPomodorosCount(habitId)
            val totalTime = dao.getTotalAccumulatedTime(habitId) ?: 0
            _pomodorosPorHabito.value = mapOf(habitId to count)
            _totalSessionTime.value = totalTime
        }
    }

    private fun loadPomodorosToday(habitId: Int) {
        viewModelScope.launch {
            val count = dao.getTodayPomodorosCount(habitId)
            _pomodorosToday.value = count
            Log.d("TimerViewModel", "Pomodoros hoy desde DB: $count")
        }
    }

    private fun loadAccumulatedTime(habitId: Int) {
        viewModelScope.launch {
            val total = dao.getTotalAccumulatedTime(habitId) ?: 0
            _totalSessionTime.value = total
            Log.d("TimerViewModel", "Tiempo total desde DB: $total segundos")
        }
    }

    fun refreshHabitData(habitId: Int) {
        viewModelScope.launch {
            val pomodoros = dao.getTodayPomodorosCount(habitId)
            val tiempo = dao.getTotalAccumulatedTime(habitId) ?: 0

            _pomodorosPorHabito.update { it + (habitId to pomodoros) }
            _tiempoPorHabito.update { it + (habitId to tiempo) }
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
        viewModelScope.launch {
            // Guarda en base de datos
            dao.insertSession(
                PomodoroSession(
                    habitId = habitId,
                    totalTimeInSeconds = duration,
                    timestamp = System.currentTimeMillis()
                )
            )
            loadAccumulatedTime(habitId)
            loadPomodorosToday(habitId)
            // Actualiza los contadores locales (opcionalmente podrías recargar desde la BD)
            _totalSessionTime.value += duration
            _pomodorosPorHabito.update { current ->
                val currentCount = current[habitId] ?: 0
                current.toMutableMap().apply {
                    this[habitId] = currentCount + 1
                }
            }
        }
    }



    fun loadAll(habits: List<Habit>) {
        viewModelScope.launch {
            val tiempoMap = mutableMapOf<Int, Int>()
            val pomodorosMap = mutableMapOf<Int, Int>()

            habits.forEach { habit ->
                val count = dao.getTodayPomodorosCount(habit.id)
                val total = dao.getTotalAccumulatedTime(habit.id) ?: 0

                pomodorosMap[habit.id] = count
                tiempoMap[habit.id] = total
            }

            _pomodorosPorHabito.value = pomodorosMap
            _tiempoPorHabito.value = tiempoMap
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

