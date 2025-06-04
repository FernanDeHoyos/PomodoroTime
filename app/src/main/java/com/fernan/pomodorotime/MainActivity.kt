package com.fernan.pomodorotime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.fernan.pomodorotime.ui.habits.HabitsScreen
import com.fernan.pomodorotime.ui.theme.PomodoroTimeTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Habits : Screen("habits", "Hábitos", Icons.Filled.FormatListNumbered )
    object Timer : Screen("timer", "Temporizador", Icons.Filled.Timer)
    object Stats : Screen("stats", "Estadísticas", Icons.Filled.BarChart)
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTimeTheme {
                MainScreen()
            }
        }
    }
}
@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomMenu(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Habits.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Habits.route) { HabitsScreen() }
            composable(Screen.Timer.route) { TimerScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
        }
    }
}

@Composable
fun BottomMenu(navController: NavHostController) {
    val items = listOf(Screen.Habits, Screen.Timer, Screen.Stats)
    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon, // El ícono que quieres usar
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Evita crear múltiples instancias
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}



@Composable
fun TimerScreen() {
    Text(text = "Pantalla de Temporizador")
}

@Composable
fun StatsScreen() {
    Text(text = "Pantalla de Estadísticas")
}

// Función para obtener la ruta actual
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
