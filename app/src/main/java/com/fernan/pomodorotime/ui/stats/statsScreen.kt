package com.fernan.pomodorotime.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fernan.pomodorotime.ui.timer.formatTime
import com.fernan.pomodorotime.viewmodel.StatsViewModel
import androidx.compose.runtime.*
import com.fernan.pomodorotime.ui.stats.components.BarChart


@Composable
fun StatsScreen() {
    val viewModel: StatsViewModel = viewModel()
    val globalStats by viewModel.globalWeeklyTotals.collectAsState()
    val globalAvg by viewModel.globalWeeklyAverage.collectAsState()
    val perHabitStats by viewModel.perHabitStats.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text("Estadísticas Generales", style = MaterialTheme.typography.titleLarge)
            Text("Promedio semanal total: ${formatTime(globalAvg)}")
            Spacer(Modifier.height(12.dp))

            // Gráfico general
            BarChart(globalStats, modifier = Modifier.padding(vertical = 16.dp))


            Spacer(modifier = Modifier.height(24.dp))
            Text("Por hábito", style = MaterialTheme.typography.titleLarge)
        }

        perHabitStats.forEach { (habitId, stats) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Hábito #$habitId", style = MaterialTheme.typography.titleMedium)

                        BarChart(data = stats, modifier = Modifier.padding(top = 12.dp))

                        Spacer(Modifier.height(8.dp))

                        stats.forEach { stat ->
                            Text("${stat.day}: ${formatTime(stat.total)}", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}


