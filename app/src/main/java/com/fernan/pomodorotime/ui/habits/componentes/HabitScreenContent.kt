package com.fernan.pomodorotime.ui.habits.componentes


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.ui.habits.HabitItem

@Composable
fun HabitsScreenContent(
    habits: List<Habit>,
    onAddHabit: (String, String, String?, List<Int>) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Tus hábitos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(habits) { habit ->
                    HabitItem (
                        habit = habit,
                        onToggle = { onToggleHabit(habit) },
                        onDelete = { onDeleteHabit(habit) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
        }

        if (showDialog) {
            AddHabitDialog(
                onDismiss = { showDialog = false },
                onAddHabit = { title, description, reminderTime, activeDays ->
                    onAddHabit(title, description, reminderTime, activeDays)
                    showDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsScreenPreview() {
    HabitsScreenContent(
        habits = listOf(
            Habit(id = 1, title = "Leer", description = "Leer 30 minutos", activeDays = listOf(1,2,3), reminderTime = "08:00"),
            Habit(id = 2, title = "Ejercicio", description = "Correr 5km", activeDays = listOf(0,4,6), reminderTime = "07:00")
        ),
        onAddHabit = { _, _, _, _ -> },
        onToggleHabit = { _ -> },
        onDeleteHabit = { _ -> }
    )
}

