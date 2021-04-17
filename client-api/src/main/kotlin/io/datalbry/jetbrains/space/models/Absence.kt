package io.datalbry.jetbrains.space.models

import java.time.LocalDateTime

data class Absence(
    val id: String,
    val approved: Boolean?,
    val approvedAt: LocalDateTime?,
    val approvedBy: ProfileIdentifier?,
    val archived: Boolean,
    val available: Boolean,
    val description: String?,
    val icon: String,
    val member: ProfileIdentifier,
    val since: LocalDateTime,
    val till: LocalDateTime
)
