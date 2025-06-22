package com.fernan.pomodorotime.ui.habits.preview


import com.fernan.pomodorotime.data.model.Habit

// Datos de prueba para los hábitos
private val mockHabits = listOf(
    Habit(
        id = 1,
        title = "Meditar",
        description = "10 minutos de meditación matutina",
        reminderTime = "08:00",
        activeDays = listOf(1, 2, 3, 4, 5), // L-V
    ),
    Habit(
        id = 2,
        title = "Leer",
        description = "Leer 30 minutos antes de dormir",
        reminderTime = "21:30",
        activeDays = listOf(1, 3, 5, 6), // L, X, V, S
    ),
    Habit(
        id = 3,
        title = "Ejercicio",
        description = "Rutina de ejercicio de 20 minutos",
        reminderTime = "07:00",
        activeDays = listOf(2, 4, 6), // M, J, S
    )
)



