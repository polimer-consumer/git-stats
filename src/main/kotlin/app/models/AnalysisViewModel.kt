package com.polimerconsumer.app.models

import tornadofx.*
import java.time.DayOfWeek

class AnalysisViewModel : ViewModel() {
    val commitData = mutableMapOf<DayOfWeek, Int>().asObservable()

    fun updateData(analyzeTimeOfWeek: Map<DayOfWeek, Int>) {
        runLater {
            commitData.clear()
            commitData.putAll(analyzeTimeOfWeek)
        }
    }
}
