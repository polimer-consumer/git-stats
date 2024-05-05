package com.polimerconsumer.app

import com.polimerconsumer.app.models.AnalysisViewModel
import com.polimerconsumer.gitclient.GitClient
import com.polimerconsumer.gitclient.models.CommitWithFiles
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import tornadofx.Controller
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppController : Controller() {
    private val gitClient = GitClient()
    val commitsList: ObservableList<String> = observableArrayList()
    val developerPairsList: ObservableList<String> = observableArrayList()
    private var commitsRawList = listOf<CommitWithFiles>()
    private val analysisViewModel: AnalysisViewModel by inject()

    suspend fun fetchCommits(owner: String, repo: String) {
        commitsRawList = gitClient.fetchCommits(owner, repo)
        val contributors = analyzeCommits(commitsRawList)
        val developerPairs = calculateDeveloperPairs(contributors)

        updateDeveloperPairsList(developerPairs)
        updateCommitsList(commitsRawList)
        analysisViewModel.updateData(analyzeTimeOfWeek(commitsRawList))
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

    private fun analyzeTimeOfWeek(commits: List<CommitWithFiles> = commitsRawList): Map<DayOfWeek, Int> {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return commits.groupBy {
            LocalDate.parse(it.commit.author.date, formatter).dayOfWeek
        }.mapValues { (_, value) -> value.size }
    }

    fun closeClient() = gitClient.close()
}

fun <T : Comparable<T>> Pair<T, T>.sorted(): Pair<T, T> {
    return if (first <= second) this else Pair(second, first)
}
