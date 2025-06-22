package com.fernan.pomodorotime.ui.timer.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.service.TimerForegroundService
import com.fernan.pomodorotime.utils.NotificationHelper
import com.fernan.pomodorotime.ui.component.ConfirmationDialog
import com.fernan.pomodorotime.utils.startTimerService
import com.fernan.pomodorotime.utils.stopTimerService
import com.fernan.pomodorotime.viewmodel.HabitViewModel
import kotlinx.coroutines.delay

@Composable
fun TimerScreenContent(
    habit: Habit,
    pomodorosToday: Int,
    initialTimeSeconds: Int = 0,
    timeTodaySeconds: Int,
    onSaveSession: (Int) -> Unit,
    onClose: () -> Unit,
    habitViewModel: HabitViewModel = viewModel()
) {
    val context = LocalContext.current
    var totalTime by remember { mutableStateOf(initialTimeSeconds) }
    var timeLeft by remember { mutableStateOf(initialTimeSeconds) }

    var isRunning by remember { mutableStateOf(false) }
    var showConfirmCloseDialog by remember { mutableStateOf(false) }


    val colorScheme = MaterialTheme.colorScheme
    val progress = timeLeft.toFloat() / totalTime.toFloat()

    var millis by remember { mutableStateOf(0) }
    val POMODORO_MIN_DURATION = 25 * 60  // 1500 segundos

    val completedHabitsToday by habitViewModel.completedHabitsToday.collectAsState()
    val isDoneToday = completedHabitsToday.contains(habit.id)




    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTimerService(context, totalTime, habit.id.toLong())
        } else {
            stopTimerService(context)
        }

        while (isRunning && (timeLeft > 0 || millis > 0)) {
            delay(10L)
            millis -= 10
            if (millis < 0) {
                millis = 990
                timeLeft--
            }
            if (timeLeft == 0 && millis <= 0) {
                isRunning = false
                NotificationHelper.showNotification(
                    context = context,
                    title = "\uD83C\uDF1F ¡Pomodoro terminado!",
                    message = "Es hora de tomar un descanso",
                    notificationId = 1001
                )
                stopTimerService(context) // Detener también cuando termina
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = habit.title,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = colorScheme.onBackground
                ),
                modifier = Modifier.padding(bottom = 16.dp)

            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF00FFFF), Color(0xFF008080))
                        ),
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
                    text = String.format("%02d:%02d:%02d", timeLeft / 60, timeLeft % 60, millis / 10),
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pomodoros hoy: $pomodorosToday",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorScheme.onBackground
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Tiempo total hoy: %02d:%02d".format(timeTodaySeconds / 60, timeTodaySeconds % 60),
                style = TextStyle(
                        fontSize = 16.sp,
                color = colorScheme.onBackground
            ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    isRunning = !isRunning

                    if (isRunning) {
                        // Iniciar servicio en segundo plano
                        val intent = Intent(context, TimerForegroundService::class.java).apply {
                            action = TimerForegroundService.ACTION_START
                            putExtra(TimerForegroundService.EXTRA_DURATION, totalTime)
                        }
                        context.startService(intent)
                    } else {
                        // Detener servicio si se pausa
                        val stopIntent = Intent(context, TimerForegroundService::class.java).apply {
                            action = TimerForegroundService.ACTION_STOP
                        }
                        context.startService(stopIntent)
                    }
                }) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(
                    onClick = {
                        if (!isRunning && timeLeft < totalTime) {
                            val sessionDuration = totalTime - timeLeft

                            if (sessionDuration >= POMODORO_MIN_DURATION) {
                                onSaveSession(sessionDuration)
                                habitViewModel.markHabitAsCompletedByPomodoro(habit.id)
                                stopTimerService(context)
                                timeLeft = totalTime
                                millis = 0
                            } else {
                                Toast.makeText(context, "Debes completar al menos 25 minutos para guardar.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar sesión",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }


                IconButton(onClick = {
                    if ((totalTime - timeLeft) < POMODORO_MIN_DURATION) {
                        showConfirmCloseDialog = true
                    } else {
                        onClose()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar timer",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }


            }
        }
    }

    if (showConfirmCloseDialog) {
        ConfirmationDialog(
            title = "¿Salir sin guardar?",
            message = "No has completado los 25 minutos. Si cierras ahora, esta sesión no se guardará.",
            confirmButtonText = "Salir",
            cancelButtonText = "Cancelar",
            onConfirm = {
                stopTimerService(context)
                showConfirmCloseDialog = false
                onClose()
            },
            onDismiss = {
                showConfirmCloseDialog = false
            }
        )
    }


}


