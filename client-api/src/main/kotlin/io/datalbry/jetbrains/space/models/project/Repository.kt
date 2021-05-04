package io.datalbry.jetbrains.space.models.project

data class Repository(
    val id: String,
    val archived: Boolean,
    val description: String?,
    val name: String,
    val project: ProjectIdentifier,
    val packageRepositoryId: String
)
