package com.polimerconsumer.gitclient.models

import kotlinx.serialization.Serializable

@Serializable
data class Commit(val sha: String, val commit: CommitInfo)