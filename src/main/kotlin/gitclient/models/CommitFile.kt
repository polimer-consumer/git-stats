package com.polimerconsumer.gitclient.models

import kotlinx.serialization.Serializable

@Serializable
data class CommitFile(val filename: String)