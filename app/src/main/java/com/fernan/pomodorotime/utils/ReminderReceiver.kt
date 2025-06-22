package com.fernan.pomodorotime.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fernan.pomodorotime.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val habitTitle = intent.getStringExtra("habit_title") ?: return
        val habitId = intent.getIntExtra("habit_id", -1)
        val reminderTime = intent.getStringExtra("reminder_time") ?: return

        Log.d("ReminderReceiver", "¡Recordatorio activado para: $habitTitle!")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel",
                "Recordatorios de Hábitos",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val quotes = listOf(
            // Originales mejoradas y más directas
            "Tu calidad se define hoy. Conviértela en un hábito.",
            "Nuevo día, nuevo hábito. ¡Empieza ahora!",
            "Pequeños hábitos, grandes resultados. Sigue así.",
            "Menos palabras, más acción. Demuéstratelo.",
            "¿Qué eliges hoy: el placer inmediato o tu mayor meta?",
            "Tus hábitos de hoy son tu 'yo' del futuro. ¡Invierte en ti!",
            "Que tus acciones hablen más alto que tus intenciones.",
            "Tu disciplina está construyendo lo que más deseas.",
            "Eres tus acciones. ¿Qué harás ahora?",

            // Nuevas frases para iniciar el día o una tarea
            "El primer paso te saca de donde estás. ¡Da el tuyo!",
            "Hoy es un buen día para sentir orgullo por lo que haces. ¡A por ello!",
            "¿Listo/a para dar un pequeño paso hacia esa gran meta?",
            "Define tu día. No dejes que tu día te defina a ti.",

            // Nuevas frases para momentos de duda
            "El progreso no es lineal. Sigue avanzando.",
            "Recuerda por qué empezaste. Esa razón sigue siendo válida.",
            "La incomodidad es el precio del crecimiento. Estás en el camino correcto.",
            "No subestimes el poder de simplemente no rendirte.",

            // Nuevas frases para fomentar la consistencia
            "Hecho es mejor que perfecto. Completa una tarea hoy.",
            "Tu futuro se crea con lo que haces hoy, no mañana.",
            "Cada repetición cuenta. Suma una más.",
            "La constancia vence al talento. Sigue esforzándote."
        )
        val randomQuote = quotes.random()

        val notification = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.ic_launcher_background) // asegúrate de que exista
            .setContentTitle("Es hora... ")
            .setContentText("No olvides: $habitTitle")
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomQuote))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()

        notificationManager.notify(habitTitle.hashCode(), notification)

        scheduleHabitReminder(context, habitId.toLong(), habitTitle, reminderTime)
    }
}


