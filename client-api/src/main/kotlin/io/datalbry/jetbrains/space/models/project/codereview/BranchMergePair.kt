package io.datalbry.jetbrains.space.models.project.codereview

data class BranchMergePair(
    val isMerged: Boolean?,
    val repository: String,
    val source: BranchInformation?,
    val target: BranchInformation?
)