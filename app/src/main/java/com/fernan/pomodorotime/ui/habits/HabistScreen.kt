package com.fernan.pomodorotime.ui.habits

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
import com.fernan.pomodorotime.ui.habits.componentes.AddHabitDialog
import com.fernan.pomodorotime.ui.habits.componentes.HabitsScreenContent
import com.fernan.pomodorotime.viewmodel.HabitViewModel

@Preview
@Composable
fun HabitsScreen(viewModel: HabitViewModel = viewModel()) {
    val habits by viewModel.habits.collectAsState()

    HabitsScreenContent(
        habits = habits,
        onAddHabit = { title, description, reminderTime, selectedDay ->
            viewModel.addHabit(title, description, reminderTime, selectedDay)
        },
        onToggleHabit = { habit ->
            viewModel.toggleHabit(habit.id)
        },
        onDeleteHabit = { habit ->
            viewModel.deleteHabit(habit.id)
        }
    )
}


