package io.datalbry.jetbrains.space.models

import java.time.LocalDateTime

data class Blog(
    val id: String,
    val archived: Boolean,
    val archivedAt: LocalDateTime?,
    val archivedBy: ProfileIdentifier?,
    val author: ProfileIdentifier,
    val created: LocalDateTime,
    val title: String,
)
