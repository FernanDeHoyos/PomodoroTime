package com.fernan.pomodorotime.data.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("habit_title") ?: "Hábito"
        NotificationHelper.showNotification(
            context,
            "Recordatorio de hábito",
            "¡Recuerda empezar: $title!",
            2000
        )
    }
}
