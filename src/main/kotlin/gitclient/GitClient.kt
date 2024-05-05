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
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json

class GitClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header(HttpHeaders.UserAgent, "ktor-client")
        }
    }

    suspend fun fetchCommits(owner: String, repo: String, apiKey: String): List<CommitWithFiles> = coroutineScope {
        val semaphore = Semaphore(10)

        try {
            val fetchResponse: HttpResponse =
                client.get("https://api.github.com/repos/$owner/$repo/commits?per_page=1000") {
                    if (apiKey.isNotEmpty()) {
                        header(HttpHeaders.Authorization, "token $apiKey")
                    }
                }
            if (!fetchResponse.status.isSuccess()) {
                println("Failed to fetch commits: ${fetchResponse.status}")
                return@coroutineScope emptyList()
            }

            val commitSHAs: List<Commit> = fetchResponse.body()

            commitSHAs.map { sha ->
                async(Dispatchers.IO) {
                    semaphore.withPermit {
                        try {
                            val detailedResponse: HttpResponse =
                                client.get("https://api.github.com/repos/$owner/$repo/commits/${sha.sha}") {
                                    if (apiKey.isNotEmpty()) {
                                        header(HttpHeaders.Authorization, "token $apiKey")
                                    }
                                }
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
                }
            }.awaitAll().filterNotNull()
        } catch (e: Exception) {
            println("Error fetching commits: ${e.message}")
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}
