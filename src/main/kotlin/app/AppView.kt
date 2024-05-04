package com.polimerconsumer.app

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.*
import tornadofx.*

class AppView : View("GitHub Commit Viewer") {
    private val controller: AppController by inject()
    private val owner = SimpleStringProperty()
    private val repo = SimpleStringProperty()

    override val root = vbox {
        label("GitHub Repository Owner:")
        textfield(owner)

        label("Repository Name:")
        textfield(repo)

        button("Fetch Commits") {
            action {
                runBlocking {
                    controller.fetchCommits(owner.value, repo.value)
                }
            }
        }

        listview(controller.commitsList)
    }

    override fun onDelete() {
        controller.closeClient()
        super.onDelete()
    }
}
