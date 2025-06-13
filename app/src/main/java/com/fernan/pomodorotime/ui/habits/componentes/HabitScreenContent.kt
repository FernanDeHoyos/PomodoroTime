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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernan.pomodorotime.Screen
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.ui.habits.HabitItem
import com.fernan.pomodorotime.ui.timer.component.TimerScreenContent
import com.fernan.pomodorotime.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreenContent(
    navController: NavHostController,
    habits: List<Habit>,
    onAddHabit: (String, String, String?, List<Int>) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    viewModel: TimerViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var selectedHabitForTimer by remember { mutableStateOf<Habit?>(null) }

    val pomodorosPorHabito by viewModel.pomodorosPorHabito.collectAsState()
    val pomodorosToday = selectedHabitForTimer?.id?.let { pomodorosPorHabito[it] } ?: 0

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val totalSessionTime by viewModel.totalSessionTime.collectAsState()
    val elapsedSeconds by viewModel.time.collectAsState()




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
                            selectedHabitForTimer = habit
                            coroutineScope.launch {
                                sheetState.show()
                            }
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
                habit = habitToEdit,
                onDismiss = { showDialog = false },
                onAddHabit = { title, description, reminderTime, activeDays ->
                    if (habitToEdit == null) {
                        onAddHabit(title, description, reminderTime, activeDays)
                    } else {
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

        // Modal Bottom Sheet para el temporizador
        if (selectedHabitForTimer != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedHabitForTimer = null
                },
                sheetState = sheetState
            ) {
                TimerScreenContent(
                    habit = selectedHabitForTimer!!,
                    initialTimeSeconds = 25,
                    pomodorosToday = pomodorosToday, // Puedes calcular esto si tienes los datos
                    timeTodaySeconds = totalSessionTime,
                    onSaveSession = {  duration ->
                        viewModel.saveSession(selectedHabitForTimer!!.id, duration) },
                    onClose = {
                        coroutineScope.launch { sheetState.hide() }
                        selectedHabitForTimer = null
                    }
                )



            }
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

