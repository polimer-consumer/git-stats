package com.polimerconsumer.app

import com.polimerconsumer.app.models.AppViewModel
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AppView : View("GitHub Commit Viewer") {
    private val model: AppViewModel by inject()
    private val owner = SimpleStringProperty()
    private val repo = SimpleStringProperty()

    override val root = tabpane {
        tab("Overview") {
            vbox {
                label("GitHub Repository Owner:")
                textfield(owner)

                label("Repository Name:")
                textfield(repo)

                button("Fetch Commits and calculate pairs") {
                    action {
                        model.fetchCommits(owner.value, repo.value)
                    }
                }

                listview(model.commitsList) {
                    prefHeight = 200.0
                }
                label("Most frequent pairs:")
                listview(model.developerPairsList) {
                    prefHeight = 200.0
                }
            }
        }

        tab("Weekday analysis") {
            this += find(AnalysisView::class)
        }
    }

    override fun onDelete() {
        model.closeClient()
        super.onDelete()
    }
}
