package com.polimerconsumer.gitclient.models

import kotlinx.serialization.Serializable

@Serializable
data class CommitInfo(val author: CommitAuthor, val message: String)