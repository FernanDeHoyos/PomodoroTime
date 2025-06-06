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
import androidx.navigation.NavHostController
import com.fernan.pomodorotime.Screen
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.ui.habits.HabitItem

@Composable
fun HabitsScreenContent(
    navController: NavHostController,
    habits: List<Habit>,
    onAddHabit: (String, String, String?, List<Int>) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onViewHabit: (Habit) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }

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
                    HabitItem(
                        habit = habit,
                        onToggle = { onToggleHabit(habit) },
                        onDelete = { onDeleteHabit(habit) },
                        onClick = {
                            habitToEdit = habit
                            showDialog = true
                        },
                        onViewHabit = {
                            navController.navigate(Screen.Timer.createRoute(habit.id))
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                habitToEdit = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
        }

        if (showDialog) {
            AddHabitDialog(
                habit = habitToEdit,  // pasamos el hábito a editar (puede ser null)
                onDismiss = { showDialog = false },
                onAddHabit = { title, description, reminderTime, activeDays ->
                    if (habitToEdit == null) {
                        onAddHabit(title, description, reminderTime, activeDays)
                    } else {
                        // Puedes crear una función onEditHabit que reciba el hábito actualizado
                        val updatedHabit = habitToEdit!!.copy(
                            title = title,
                            description = description,
                            reminderTime = reminderTime,
                            activeDays = activeDays
                        )
                        onEditHabit(updatedHabit)
                    }
                    showDialog = false
                }
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun HabitsScreenPreview() {
//    HabitsScreenContent(
//        habits = listOf(
//            Habit(id = 1, title = "Leer", description = "Leer 30 minutos", activeDays = listOf(1,2,3), reminderTime = "08:00"),
//            Habit(id = 2, title = "Ejercicio", description = "Correr 5km", activeDays = listOf(0,4,6), reminderTime = "07:00")
//        ),
//        onAddHabit = { _, _, _, _ -> },
//        onToggleHabit = { _ -> },
//        onDeleteHabit = { _ -> },
//        onEditHabit = { _ -> },
//        onViewHabit = { _ -> },
//    )
//}

