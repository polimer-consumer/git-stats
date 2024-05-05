package com.polimerconsumer.app

import com.polimerconsumer.app.models.AnalysisViewModel
import javafx.collections.MapChangeListener
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*
import java.time.DayOfWeek

class AnalysisView : View("Commit Analysis") {
    private val model: AnalysisViewModel by inject()
    private val commitsSeries = XYChart.Series<String, Number>()

    init {
        model.commitData.addListener { _: MapChangeListener.Change<out DayOfWeek, out Int> ->
            runLater {
                updateChartData()
            }
        }
    }

    override val root = vbox {
        barchart("Commits by Day of the Week", CategoryAxis(), NumberAxis()) {
            data.add(commitsSeries)
        }
    }

    private fun updateChartData() {
        commitsSeries.data.clear()
        model.commitData.forEach { (day, count) ->
            commitsSeries.data.add(XYChart.Data(day.name, count))
        }
    }

    override fun onDock() {
        updateChartData()
    }
}
