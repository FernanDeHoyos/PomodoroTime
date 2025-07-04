package com.fernan.pomodorotime.ui.habits.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.data.model.Habit

@Composable
fun AddHabitDialog(
    habit: Habit? = null,
    onDismiss: () -> Unit,
    onAddHabit: (String, String, String?, List<Int>) -> Unit // now includes reminderTime
) {

   // var title by remember { mutableStateOf("") }
    //var description by remember { mutableStateOf("") }

    val weekDays = listOf("L", "M", "X", "J", "V", "S", "D") // 0 a 6
   // var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(habit?.reminderTime?.substringBefore(":")?.toInt() ?: 8) }
    var selectedMinute by remember { mutableStateOf(habit?.reminderTime?.substringAfter(":")?.toInt() ?: 0) }

    var reminderTime by remember { mutableStateOf(habit?.reminderTime ?: "") }

    var title by remember { mutableStateOf(habit?.title ?: "") }
    var description by remember { mutableStateOf(habit?.description ?: "") }
    //var reminderTime by remember { mutableStateOf(habit?.reminderTime ?: "") }
    var selectedDays by remember { mutableStateOf(habit?.activeDays ?: emptyList()) }
    LaunchedEffect(selectedHour, selectedMinute) {
        reminderTime = String.format("%02d:%02d", selectedHour, selectedMinute)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo hábito") },
        modifier = Modifier.background(Color.Transparent),
        containerColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

                // Campo Título: solo una línea con línea inferior (estilo 'underline')
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Ajusta la altura para simular textarea
                    maxLines = 5,
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Hora de recordatorio:")
                Button(onClick = { showTimePicker = true }) {
                    Text(reminderTime)
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weekDays.forEachIndexed { index, day ->
                        AssistChip(
                            onClick = {
                                selectedDays = if (selectedDays.contains(index)) {
                                    selectedDays - index
                                } else {
                                    selectedDays + index
                                }
                            },
                            label = { Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(5f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                            },
                            modifier = Modifier.weight(1f),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedDays.contains(index)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddHabit(title, description, reminderTime, selectedDays.toList())
                        onDismiss()
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeChange = { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                showTimePicker = false
            },
            initialHour = selectedHour,
            initialMinute = selectedMinute
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onTimeChange(timePickerState.hour, timePickerState.minute)
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        },
        title = { Text("Selecciona la hora") },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAddHabitDialog() {
    MaterialTheme {
        AddHabitDialog(
            onDismiss = {},
            onAddHabit = { title, description, reminderTime, days ->
                // Puedes imprimir para verificar si funciona
                println("Título: $title, Descripción: $description, Hora: $reminderTime, Días: $days")
            }
        )
    }
}

