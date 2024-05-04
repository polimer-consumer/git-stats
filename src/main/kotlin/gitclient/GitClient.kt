package com.polimerconsumer.gitclient

import com.polimerconsumer.gitclient.models.Commit
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
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            header("Accept", "application/vnd.github.v3+json")
            header("User-Agent", "ktor-client")
        }
    }

    suspend fun fetchCommits(owner: String, repo: String): List<Commit> {
        return try {
            val response: HttpResponse = client.get("https://api.github.com/repos/$owner/$repo/commits")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                println("Failed to fetch commits: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching commits: ${e.message}")
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}
