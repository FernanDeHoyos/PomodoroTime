package com.fernan.pomodorotime.utils

import java.util.Calendar
import java.util.Date

fun parseHour(reminderTime: String?): Int? = reminderTime?.split(":")?.getOrNull(0)?.toIntOrNull()

fun getHoursRange(): List<String> = (6..22).map { String.format("%02d:00", it) }

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
