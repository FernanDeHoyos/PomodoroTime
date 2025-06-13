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
import com.fernan.pomodorotime.ui.timer.component.NumberSelector
import com.fernan.pomodorotime.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    //val time by viewModel.time.collectAsState()
    //val time by viewModel.remainingTime.collectAsState()
    val time by viewModel.time.collectAsState()


    val millis by viewModel.millis.collectAsState()
    val maxTime by viewModel.sessionDuration.collectAsState()
    val progress = if (maxTime > 0) time / maxTime.toFloat() else 0f

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberSelector(range = 0..23, selected = hours, onSelectedChange = { hours = it }, label = "Horas")
            NumberSelector(range = 0..59, selected = minutes, onSelectedChange = { minutes = it }, label = "Min")
            NumberSelector(range = 0..59, selected = seconds, onSelectedChange = { seconds = it }, label = "Seg")
        }


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
            Button(onClick = {
                viewModel.setInitialTime(
                    minutes = (hours * 60) + minutes,
                    seconds = seconds
                )
                viewModel.startTimer()
            }) { Text("Iniciar") }

            Button(onClick = { viewModel.stopTimer() }) { Text("Pausar") }
            Button(onClick = { viewModel.resetTimer() }) { Text("Reiniciar") }
        }
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
