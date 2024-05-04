package com.polimerconsumer.gitclient.models

import kotlinx.serialization.Serializable

@Serializable
data class CommitAuthor(val name: String, val date: String)