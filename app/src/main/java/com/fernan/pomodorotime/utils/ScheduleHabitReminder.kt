package com.fernan.pomodorotime.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

fun scheduleHabitReminder(context: Context, habitId: Long, habitTitle: String, reminderTime: String = "00:00") {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("habit_title", habitTitle)
        putExtra("habit_id", habitId.toInt())
        putExtra("reminder_time", reminderTime)
    }


    val pendingIntent = PendingIntent.getBroadcast(
        context,
        habitId.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val (hour, minute) = reminderTime.split(":").map { it.toInt() }

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MINUTE, -10) // 🔔 Notificar 10 minutos antes de la hora programada
    }


    val triggerTime = calendar.timeInMillis

    Log.d("AlarmTest", "Programando alarma para: ${calendar.time}")


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Log.w("AlarmTest", "No se puede programar alarmas exactas.")
            return
        }
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )

}


