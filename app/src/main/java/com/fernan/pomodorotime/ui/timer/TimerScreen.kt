package com.fernan.pomodorotime.ui.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fernan.pomodorotime.viewmodel.TimerViewModel
import com.fernan.pomodorotime.viewmodel.HabitViewModel
import com.fernan.pomodorotime.data.model.Habit

/**
 * Composable que representa la pantalla principal del temporizador Pomodoro.
 * Incluye visualización del tiempo, controles para iniciar/pausar/reiniciar,
 * y un DropdownMenu para seleccionar un hábito activo.
 */
@Composable
fun TimerScreen(habitId: Int, viewModel: TimerViewModel = viewModel()) {
    LaunchedEffect(habitId) {
        viewModel.setHabitId(habitId)
    }

    val time by viewModel.time.collectAsState()
    val millis by viewModel.millis.collectAsState()
    val totalTime by viewModel.totalSessionTime.collectAsState()
    val maxTime = viewModel.sessionDuration
    val primaryColor = MaterialTheme.colorScheme.primary
    val progress = (time + millis / 1000f) / maxTime.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp)) {
                drawCircle(
                    color = Color.LightGray,
                    style = Stroke(width = 16f)
                )
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
            }

            Text(
                text = formatTime(time, millis),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { viewModel.startTimer() }) { Text("Iniciar") }
            Button(onClick = { viewModel.stopTimer() }) { Text("Pausar") }
            Button(onClick = { viewModel.resetTimer() }) { Text("Reiniciar") }
        }

        Button(onClick = { viewModel.saveSession() }) {
            Text("Guardar sesión")
        }

        Text(
            text = "Total acumulado: ${formatTime(totalTime)}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Formatea los valores de segundos y milisegundos en formato mm:ss:cc.
 */
fun formatTime(seconds: Int, millis: Int = 0): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    val ms = (millis % 1000) / 10
    return String.format("%02d:%02d:%02d", minutes, secs, ms)
}
