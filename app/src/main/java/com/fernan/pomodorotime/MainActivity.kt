package com.fernan.pomodorotime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fernan.pomodorotime.ui.habits.HabitsScreen
import com.fernan.pomodorotime.ui.stats.StatsScreen
import com.fernan.pomodorotime.ui.theme.PomodoroTimeTheme
import com.fernan.pomodorotime.ui.timer.TimerScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Habits : Screen("habits", "Hábitos", Icons.Filled.FormatListNumbered )
    object Timer : Screen("timer", "Temporizador", Icons.Filled.Timer )
    object Stats : Screen("stats", "Estadísticas", Icons.Filled.BarChart)
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val openTimer = intent?.getBooleanExtra("open_timer", false) ?: false
        val habitId = intent?.getLongExtra("habit_id", -1L) ?: -1L

        setContent {
            PomodoroTimeTheme {
                MainScreen(openTimer, habitId)
            }
        }

    }
}

@Composable
fun MainScreen(openTimer: Boolean, habitId: Long) {
    val navController = rememberNavController()


    val darkTheme = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorScheme.primary,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.onError,
            darkIcons = !darkTheme
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.primary
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.primary,
                bottomBar = { BottomMenu(navController) },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Habits.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Habits.route) {
                        HabitsScreen(navController = navController, openTimer = openTimer, habitId = habitId)
                    }

                    composable(Screen.Timer.route) { TimerScreen() }
                    composable(Screen.Stats.route) { StatsScreen() }
                }
            }
        }
    }
}


@Composable
fun BottomMenu(navController: NavHostController) {
    val items = listOf(Screen.Habits, Screen.Timer, Screen.Stats)
    val colorScheme = MaterialTheme.colorScheme
    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = !darkTheme
        )
    }

    NavigationBar(
        containerColor = colorScheme.background,
        modifier = Modifier
            .shadow(8.dp)
            .height(80.dp),

        ) {
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





// Función para obtener la ruta actual
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
