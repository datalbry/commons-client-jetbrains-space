package io.datalbry.jetbrains.space.models.project

data class IssueIdentifier(
    val issueId: String,
    val projectKey: String,
    val issueNumber: Int
)
