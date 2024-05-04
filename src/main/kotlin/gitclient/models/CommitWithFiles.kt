package com.polimerconsumer.gitclient.models

import kotlinx.serialization.Serializable

@Serializable
data class CommitWithFiles(val sha: String, val commit: CommitInfo, val files: List<CommitFile>?)