package com.fernan.pomodorotime.ui.habits.componentes


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.fernan.pomodorotime.data.model.Habit
import com.fernan.pomodorotime.ui.habits.HabitItem
import androidx.compose.ui.graphics.Color
import com.fernan.pomodorotime.viewmodel.HabitViewModel


@Composable
fun HabitHourList(
    hours: List<String>,
    groupedHabits: Map<Int, List<Habit>>,
    onToggleHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onClickEdit: (Habit) -> Unit,
    onViewHabit: (Habit) -> Unit,
    habitViewModel: HabitViewModel
) {

    val completedHabitsToday by habitViewModel.completedHabitsToday.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .fillMaxHeight()
            .width(45.dp)
        ) {
            drawLine(
                color = Color.Gray,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(hours) { hour ->
                val hourInt = hour.split(":")[0].toIntOrNull() ?: -1
                val habitsAtHour = groupedHabits[hourInt].orEmpty()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = hour,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier
                            .width(45.dp)
                            .padding(start = 8.dp, top = 8.dp)
                    )

                    Column(modifier = Modifier.padding(2.dp)) {
                        habitsAtHour.forEach { habit ->
                            val isDoneToday = completedHabitsToday.contains(habit.id)
                            HabitItem(
                                habit = habit,
                                onToggle = { onToggleHabit(habit) },
                                onDelete = { onDeleteHabit(habit) },
                                onClick = { onClickEdit(habit) },
                                onViewHabit = { onViewHabit(habit) },
                                isDoneToday = isDoneToday
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
