package com.fernan.pomodorotime.utils

import android.content.Context
import android.content.Intent
import com.fernan.pomodorotime.service.TimerForegroundService

fun startTimerService(context: Context, duration: Int, habitId: Long) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_START
        putExtra(TimerForegroundService.EXTRA_DURATION, duration)
        putExtra("habit_id", habitId) // ✅ Incluye el habitId aquí
    }
    context.startService(intent)
}


fun stopTimerService(context: Context) {
    val intent = Intent(context, TimerForegroundService::class.java).apply {
        action = TimerForegroundService.ACTION_STOP
    }
    context.startService(intent)
}
