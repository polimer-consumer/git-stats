package com.polimerconsumer.app.models

import com.polimerconsumer.app.AppController
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import tornadofx.*
import kotlinx.coroutines.*

class AppViewModel : ViewModel() {
    private val controller: AppController by inject()

    val commitsList = SimpleListProperty(FXCollections.observableArrayList<String>())
    val developerPairsList = SimpleListProperty(FXCollections.observableArrayList<String>())

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun fetchCommits(owner: String, repo: String, apiKey: String) {
        viewModelScope.launch {
            controller.fetchCommits(owner, repo, apiKey)
            runLater {
                commitsList.setAll(controller.commitsList)
                developerPairsList.setAll(controller.developerPairsList)
            }
        }
    }

    fun closeClient() {
        viewModelScope.launch {
            controller.closeClient()
        }
    }
}
