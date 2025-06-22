package com.fernan.pomodorotime.utils


fun parseHour(reminderTime: String?): Int? = reminderTime?.split(":")?.getOrNull(0)?.toIntOrNull()

fun getHoursRange(): List<String> = (6..22).map { String.format("%02d:00", it) }


