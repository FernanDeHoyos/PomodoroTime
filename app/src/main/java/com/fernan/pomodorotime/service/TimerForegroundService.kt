package com.fernan.pomodorotime.service

// TimerForegroundService.kt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fernan.pomodorotime.MainActivity
import kotlinx.coroutines.*

class TimerForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "PomodoroTimerChannel"
        const val NOTIFICATION_ID = 101
        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"
        const val EXTRA_DURATION = "DURATION"
    }

    private var timerJob: Job? = null
    private var habitId: Long = -1L
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START -> {
                val duration = intent.getIntExtra(EXTRA_DURATION, 0)
                Log.d("FERNAN - TimerService", "Start with duration: $duration")
                habitId = intent.getLongExtra("habit_id", -1L)
                Handler(Looper.getMainLooper()).post {
                    startForeground(NOTIFICATION_ID, createNotification(duration))
                }
                startTimer(duration)
            }

            ACTION_STOP -> {
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun startTimer(duration: Int) {
        timerJob = serviceScope.launch {
            var remainingTime = duration
            while (remainingTime > 0) {
                delay(1000L)
                remainingTime--
                updateNotification(remainingTime)
            }
            stopForeground(true)
            stopSelf()
        }
    }

    private fun createNotification(secondsLeft: Int): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        // 🔁 PendingIntent para abrir MainActivity al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_timer", true)
            putExtra("habit_id", habitId) // ← Asegúrate de tener esto como propiedad de tu servicio
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro activo")
            .setContentText("Tiempo restante: ${formatTime(secondsLeft)}")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setContentIntent(pendingIntent) // 🟢 Agregado
            .setOnlyAlertOnce(true) // No vibra/sonido al actualizar
            .build()
    }


    private fun updateNotification(secondsLeft: Int) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro activo")
            .setContentText("Tiempo restante: ${formatTime(secondsLeft)}")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }


    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(minutes, sec)
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
