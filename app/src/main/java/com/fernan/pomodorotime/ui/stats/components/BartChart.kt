package com.fernan.pomodorotime.ui.stats.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.fernan.pomodorotime.data.dao.DayTotal


@Composable
fun BarChart(data: List<DayTotal>, modifier: Modifier = Modifier){
    AndroidView(
        factory = { context ->
            AnyChartView(context).apply {
                val cartesian = AnyChart.column()

                val chartData = data.map {
                    ValueDataEntry(it.day.take(3), it.total)
                }

                cartesian.data(chartData)
                cartesian.title("Pomodoros por Día")
                cartesian.yAxis(0).title("Segundos")
                cartesian.xAxis(0).title("Día")

                this.setChart(cartesian)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

