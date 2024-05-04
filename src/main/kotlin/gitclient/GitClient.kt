package com.polimerconsumer.gitclient

import com.polimerconsumer.gitclient.models.Commit
import com.polimerconsumer.gitclient.models.CommitWithFiles
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class GitClient {
    private val token = "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"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header(HttpHeaders.Authorization, "token $token")
            header(HttpHeaders.UserAgent, "ktor-client")
        }
    }

    suspend fun fetchCommits(owner: String, repo: String): List<CommitWithFiles> {
        try {
            val fetchResponse: HttpResponse =
                client.get("https://api.github.com/repos/$owner/$repo/commits?per_page=10000")
            if (!fetchResponse.status.isSuccess()) {
                println("Failed to fetch commits: ${fetchResponse.status}")
                return emptyList()
            }

            val commitSHAs: List<Commit> = fetchResponse.body()

            val commits = commitSHAs.mapNotNull { sha ->
                try {
                    val detailedResponse: HttpResponse =
                        client.get("https://api.github.com/repos/$owner/$repo/commits/${sha.sha}")
                    if (detailedResponse.status.isSuccess()) {
                        detailedResponse.body<CommitWithFiles>()
                    } else {
                        println("Failed to fetch detailed commit info for SHA ${sha.sha}: ${detailedResponse.status}")
                        null
                    }
                } catch (e: Exception) {
                    println("Error fetching detailed commit info for SHA ${sha.sha}: ${e.message}")
                    null
                }
            }

            println("commits fetched")
            return commits
        } catch (e: Exception) {
            println("Error fetching commits: ${e.message}")
            return emptyList()
        }
    }

    fun close() {
        client.close()
    }
}
