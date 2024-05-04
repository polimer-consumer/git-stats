package com.polimerconsumer.app

import com.polimerconsumer.gitclient.GitClient
import javafx.collections.FXCollections.observableArrayList
import tornadofx.Controller

class AppController : Controller() {
    private val gitClient = GitClient()
    val commitsList = observableArrayList<String>()

    suspend fun fetchCommits(owner: String, repo: String) {
        val commits = gitClient.fetchCommits(owner, repo)
        commitsList.setAll(commits.map {
            "${it.commit.message} : ${it.commit.author.name}. Date: ${it.commit.author.date}"
        })
    }

    fun closeClient() = gitClient.close()
}
