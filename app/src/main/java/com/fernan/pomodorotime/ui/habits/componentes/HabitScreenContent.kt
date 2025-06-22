// HabitsScreen.kt
package com.fernan.pomodorotime.ui.habits.componentes

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.utils.getHoursRange
import com.fernan.pomodorotime.utils.parseHour
import com.fernan.pomodorotime.ui.timer.component.TimerScreenContent
import com.fernan.pomodorotime.viewmodel.TimerViewModel
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.ui.component.ConfirmationDialog
import com.fernan.pomodorotime.viewmodel.HabitViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreenContent(
    navController: NavHostController,
    habits: List<Habit>,
    onAddHabit: (String, String, String?, List<Int>) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    viewModel: TimerViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel(),
    openTimer: Boolean,
    habitIdToOpen: Long
) {


    // Estados
    val hours = getHoursRange()
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEditConfirmDialog by remember { mutableStateOf(false) }

    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var selectedHabitForTimer by remember { mutableStateOf<Habit?>(null) }

    val calendar = remember { Calendar.getInstance() }
    val currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    val context = LocalContext.current
    val completedHabitsToday by habitViewModel.completedHabitsToday.collectAsState()


    val pomodorosPorHabito by viewModel.pomodorosPorHabito.collectAsState()
    val tiemposPorHabito by viewModel.tiempoPorHabito.collectAsState()
    val groupedHabits = habits
        .filter { !it.reminderTime.isNullOrBlank() }
        .groupBy { parseHour(it.reminderTime) ?: -1 }

    val pomodorosToday = selectedHabitForTimer?.id?.let { pomodorosPorHabito[it] } ?: 0
    val totalSessionTime = selectedHabitForTimer?.id?.let { tiemposPorHabito[it] } ?: 0

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(habits) {
        viewModel.loadAll(habits)
    }

    LaunchedEffect(openTimer, habitIdToOpen, habits) {
        if (openTimer && habitIdToOpen != -1L) {
            val habit = habits.find { it.id.toLong() == habitIdToOpen }
            if (habit != null) {
                selectedHabitForTimer = habit
                viewModel.refreshHabitData(habit.id)
                coroutineScope.launch { sheetState.show() }
            }
        }
    }



    fun onHabitClick(habit: Habit) {
        if (completedHabitsToday.contains(habit.id)) {
            Toast.makeText(context, "Este hábito ya fue completado hoy.", Toast.LENGTH_SHORT).show()
        } else {
            selectedHabitForTimer = habit
            viewModel.refreshHabitData(habit.id)
            coroutineScope.launch { sheetState.show() }
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    habitToEdit = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 4.dp // opcional: da sombra
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    CalendarHeader(currentMonth, currentYear)
                    DaySelector()
                }
            }



            HabitHourList(
                hours = hours,
                groupedHabits = groupedHabits,
                onToggleHabit = onToggleHabit,
                onDeleteHabit = onDeleteHabit,
                onClickEdit = {
                    habitToEdit = it
                    showDialog = true
                },
                onViewHabit = { habit -> onHabitClick(habit) },

                habitViewModel = habitViewModel
            )
        }
    }

    if (showDialog) {
        AddHabitDialog(
            habit = habitToEdit,
            onDismiss = { showDialog = false },
            onAddHabit = { title, desc, time, days ->
                if (habitToEdit == null) {
                    onAddHabit(title, desc, time, days)
                    showDialog = false
                } else {
                    // Mostrar confirmación de edición
                    showEditConfirmDialog = true

                    // Guarda temporalmente los datos nuevos hasta que el usuario confirme
                    habitToEdit = habitToEdit!!.copy(
                        title = title,
                        description = desc,
                        reminderTime = time,
                        activeDays = days
                    )
                }
            }
                )
    }


    if (showEditConfirmDialog) {
        ConfirmationDialog(
            title = "¿Guardar cambios?",
            message = "¿Deseas actualizar este hábito con los nuevos valores?",
            confirmButtonText = "Guardar",
            cancelButtonText = "Cancelar",
            onConfirm = {
                habitToEdit?.let { onEditHabit(it) }
                showDialog = false
                showEditConfirmDialog = false
            },
            onDismiss = {
                showEditConfirmDialog = false
            }
        )
    }




    if (selectedHabitForTimer != null) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
                selectedHabitForTimer = null
            },
            sheetState = sheetState
        ) {
            TimerScreenContent(
                habit = selectedHabitForTimer!!,
                initialTimeSeconds = 1500,
                pomodorosToday = pomodorosToday,
                timeTodaySeconds = totalSessionTime,
                onSaveSession = { duration ->
                    val habitId = selectedHabitForTimer!!.id
                    viewModel.saveSession(habitId, duration)
                    viewModel.setHabit(habitId)
                },
                onClose = {
                    coroutineScope.launch { sheetState.hide() }
                    selectedHabitForTimer = null
                },

            )
        }
    }
}



