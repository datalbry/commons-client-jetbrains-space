package io.datalbry.jetbrains.space.models.project.codereview

import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import io.datalbry.jetbrains.space.models.project.ProjectIdentifier
import java.time.LocalDateTime

sealed class CodeReview {
    abstract val id: String
    abstract val canBeReopened: Boolean?
    abstract val createdAt: LocalDateTime
    abstract val createdBy: ProfileIdentifier?
    abstract val number: Int
    abstract val projectKey: String
    abstract val projectIdentifier: ProjectIdentifier
    abstract val state: String
    abstract val title: String
    abstract val turnBased: Boolean?
}

data class MergeRequestCodeReview(
    override val id: String,
    override val canBeReopened: Boolean?,
    override val createdAt: LocalDateTime,
    override val createdBy: ProfileIdentifier?,
    override val number: Int,
    override val projectKey: String,
    override val projectIdentifier: ProjectIdentifier,
    override val state: String,
    override val title: String,
    override val turnBased: Boolean?,
    val branchInformation: List<BranchMergePair>
) : CodeReview()

data class CommitSetCodeReview(
    override val id: String,
    override val canBeReopened: Boolean?,
    override val createdAt: LocalDateTime,
    override val createdBy: ProfileIdentifier?,
    override val number: Int,
    override val projectKey: String,
    override val projectIdentifier: ProjectIdentifier,
    override val state: String,
    override val title: String,
    override val turnBased: Boolean?
) : CodeReview()