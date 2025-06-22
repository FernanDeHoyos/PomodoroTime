package com.fernan.pomodorotime.ui.habits.componentes

import androidx.compose.runtime.Composable


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import java.util.Date

@Composable
fun DaySelector(
    selectedDate: Date = Date(),
    modifier: Modifier = Modifier,
    onDateSelected: (Date) -> Unit = {}
) {
    val daysToShow = 7
    val calendar = remember { Calendar.getInstance() }
    val today = calendar.time

    val daysList = remember {
        val firstDayOfWeek = calendar.firstDayOfWeek
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

        List(daysToShow) {
            val day = calendar.time
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            day
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysList.forEach { date ->
            DayItem(
                date = date,
                isToday = isSameDay(date, today),
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DayItem(
    date: Date,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val dayCalendar = Calendar.getInstance().apply { time = date }
    val dayOfWeek = when (dayCalendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "L"
        Calendar.TUESDAY -> "M"
        Calendar.WEDNESDAY -> "X"
        Calendar.THURSDAY -> "J"
        Calendar.FRIDAY -> "V"
        Calendar.SATURDAY -> "S"
        Calendar.SUNDAY -> "D"
        else -> ""
    }
    val colorScheme = MaterialTheme.colorScheme


    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(
                if (isToday) colorScheme.onPrimary
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.labelSmall,
                color = if (isToday) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = dayCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (isToday) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

@Preview
@Composable
fun DaySelectorPreview() {
    MaterialTheme {
        DaySelector()
    }
}