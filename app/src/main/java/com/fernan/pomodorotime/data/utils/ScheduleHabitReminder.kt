package com.fernan.pomodorotime.data.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleHabitReminder(context: Context, habitId: Long, habitTitle: String, startTimeMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("habit_title", habitTitle)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        habitId.toInt(), // usa un ID único por hábito
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Programa 5 minutos antes del inicio
    val reminderTimeMillis = startTimeMillis - (5 * 60 * 1000)

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        reminderTimeMillis,
        pendingIntent
    )
}
