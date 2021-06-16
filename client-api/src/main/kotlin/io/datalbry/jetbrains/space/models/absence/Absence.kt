package io.datalbry.jetbrains.space.models.absence

import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
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
