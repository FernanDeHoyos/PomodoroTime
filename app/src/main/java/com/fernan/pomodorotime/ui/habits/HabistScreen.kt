package com.fernan.pomodorotime.ui.habits

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernan.pomodorotime.Screen
import com.fernan.pomodorotime.ui.habits.componentes.HabitsScreenContent
import com.fernan.pomodorotime.viewmodel.HabitViewModel

@Preview
@Composable
fun HabitsScreen(
    navController: NavHostController, // 👈 nuevo parámetro
    viewModel: HabitViewModel = viewModel()
) {
    val habits by viewModel.habits.collectAsState()

    HabitsScreenContent(
        navController = navController, // 👈 pásalo aquí también
        habits = habits,
        onAddHabit = { title, description, reminderTime, selectedDay ->
            viewModel.addHabit(title, description, reminderTime, selectedDay)
        },
        onToggleHabit = { habit ->
            viewModel.toggleHabit(habit.id)
        },
        onDeleteHabit = { habit ->
            viewModel.deleteHabit(habit.id)
        },
        onEditHabit = { updatedHabit ->
            viewModel.updateHabit(updatedHabit)
        },
        onViewHabit = { habit ->
            navController.navigate(Screen.Timer.createRoute(habit.id))
        }
    )
}



