package com.fernan.pomodorotime.ui.timer.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.data.model.Habit
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.fernan.pomodorotime.data.utils.NotificationHelper

@Composable
fun TimerScreenContent(
    habit: Habit,
    pomodorosToday: Int,
    initialTimeSeconds: Int = 0, // <- nuevo parámetro
    timeTodaySeconds: Int,
    onSaveSession: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var totalTime by remember { mutableStateOf(initialTimeSeconds) }
    var timeLeft by remember { mutableStateOf(initialTimeSeconds) }


    var isRunning by remember { mutableStateOf(false) }

    // Estados locales para mostrar pomodoros y tiempo acumulado en la UI
//    var pomodorosCount by remember { mutableStateOf(pomodorosToday) }
//    var accumulatedTime by remember { mutableStateOf(timeTodaySeconds) }

    // Progreso circular
    val progress = timeLeft.toFloat() / totalTime

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }
        if (timeLeft == 0) isRunning = false
        if (timeLeft == 0) {
            isRunning = false
            NotificationHelper.showNotification(
                context = context,
                title = "¡Pomodoro terminado!",
                message = "¡Es hora de tomar un descanso!",
                notificationId = 1001
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Blue,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 12.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                    drawArc(
                        color = Color.DarkGray,
                        startAngle = -90f,
                        sweepAngle = 360 * progress,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 12.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
                Text(
                    text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar pomodoros y tiempo total hoy
            Text(
                text = "Pomodoros hoy: $pomodorosToday",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Tiempo total hoy: %02d:%02d".format(timeTodaySeconds / 60, timeTodaySeconds % 60),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { isRunning = !isRunning }
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar"
                    )
                }

                IconButton(
                    onClick = {
                        if (!isRunning && timeLeft < totalTime) {
                            val sessionDuration = totalTime - timeLeft
                            onSaveSession(sessionDuration)

                            // Actualizar estados locales
//                            pomodorosCount++
//                            accumulatedTime += sessionDuration

                            timeLeft = totalTime
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar sesión"
                    )
                }

                IconButton(
                    onClick = onClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar timer"
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun TimerScreenContentPreview() {
    // Hábito de ejemplo
    val sampleHabit = Habit(
        id = 1,
        title = "Estudiar Kotlin",
        description = "Practicar Compose y coroutines",
        reminderTime = "ss",
        activeDays = listOf(1, 2, 3)
    )

    TimerScreenContent(
        habit = sampleHabit,
        onSaveSession = { duration ->
            println("Sesión guardada: $duration segundos")
        },
        onClose = {
            println("Timer cerrado")
        },
        timeTodaySeconds = 100,
        pomodorosToday = 1
    )
}

