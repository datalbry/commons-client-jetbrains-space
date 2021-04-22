package io.datalbry.jetbrains.space.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Issue(
    val id: String,
    val archived: Boolean,
    val assignee: ProfileIdentifier?,
    val attachmentsCount: Int?,
    val createdBy: String,
    val creationTime: LocalDateTime,
    val dueDate: LocalDate?,
    val number: Int,
    val projectId: ProjectIdentifier,

    val title: String
)