package io.datalbry.jetbrains.space.models.project.codereview

data class BranchInformation(
    val isDeleted: Boolean,
    val displayName: String,
    val head: String?,
    val reference: String,
)