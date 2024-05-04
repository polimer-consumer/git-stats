package com.polimerconsumer.app

import com.polimerconsumer.gitclient.GitClient
import com.polimerconsumer.gitclient.models.CommitWithFiles
import javafx.collections.FXCollections.observableArrayList
import tornadofx.Controller

class AppController : Controller() {
    private val gitClient = GitClient()
    val commitsList = observableArrayList<String>()
    val developerPairsList = observableArrayList<String>()

    suspend fun fetchCommits(owner: String, repo: String) {
        val commits = gitClient.fetchCommits(owner, repo)
        val contributors = analyzeCommits(commits)
        val developerPairs = calculateDeveloperPairs(contributors)
        updateDeveloperPairsList(developerPairs)
        updateCommitsList(commits)
    }

    private fun updateCommitsList(commits: List<CommitWithFiles>) {
        commitsList.setAll(commits.map { "${it.commit.message} : ${it.commit.author.name}. Date: ${it.commit.author.date}" })
    }

    private fun updateDeveloperPairsList(pairs: Map<Pair<String, String>, Int>) {
        developerPairsList.setAll(pairs.map { "${it.key.first} & ${it.key.second}: ${it.value}" }
            .sortedByDescending { it.split(": ").last().toInt() })
    }

    private fun analyzeCommits(commits: List<CommitWithFiles>): Map<String, Set<String>> {
        val fileContributors = mutableMapOf<String, MutableSet<String>>()
        commits.forEach { commit ->
            commit.files?.forEach { file ->
                fileContributors.getOrPut(file.filename) { mutableSetOf() }.add(commit.commit.author.name)
            }
        }

        return fileContributors
    }

    private fun calculateDeveloperPairs(fileContributors: Map<String, Set<String>>): Map<Pair<String, String>, Int> {
        val pairsCount = mutableMapOf<Pair<String, String>, Int>()
        fileContributors.values.forEach { contributors ->
            contributors.toList().let { list ->
                list.indices.forEach { i ->
                    (i + 1 until list.size).forEach { j ->
                        val pair = Pair(list[i], list[j]).sorted()
                        pairsCount[pair] = pairsCount.getOrDefault(pair, 0) + 1
                    }
                }
            }
        }

        return pairsCount
    }

    fun closeClient() = gitClient.close()
}

fun <T : Comparable<T>> Pair<T, T>.sorted(): Pair<T, T> {
    return if (first <= second) this else Pair(second, first)
}
