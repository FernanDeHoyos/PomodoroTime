package com.fernan.pomodorotime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fernan.pomodorotime.data.dao.DayTotal
import com.fernan.pomodorotime.data.dao.PomodoroDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    //private val dao = DatabaseProvider.getDatabase(application).pomodoroDao()
    private val dao: PomodoroDao = DatabaseProvider.getDatabase(application).pomodoroDao()
    private val _globalWeeklyTotals = MutableStateFlow<List<DayTotal>>(emptyList())
    val globalWeeklyTotals = _globalWeeklyTotals.asStateFlow()

    private val _globalWeeklyAverage = MutableStateFlow(0)
    val globalWeeklyAverage = _globalWeeklyAverage.asStateFlow()

    private val _perHabitStats = MutableStateFlow<Map<Int, List<DayTotal>>>(emptyMap())
    val perHabitStats = _perHabitStats.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startOfWeek = now - 6 * 24 * 60 * 60 * 1000

            val global = dao.getGlobalDailyTotalsLast7Days(startOfWeek)
            _globalWeeklyTotals.value = global
            _globalWeeklyAverage.value = if (global.isNotEmpty()) global.sumOf { it.total } / global.size else 0

            val habitIds = dao.getAllHabitIds()
            val habitStats = mutableMapOf<Int, List<DayTotal>>()

            for (habitId in habitIds) {
                val stats = dao.getDailyTotalsLast7Days(habitId, startOfWeek)
                habitStats[habitId] = stats
            }

            _perHabitStats.value = habitStats
        }
    }
}
