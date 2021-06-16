package io.datalbry.jetbrains.space.models.project

import java.time.LocalDateTime

data class Package(
    val lastVersion: String,
    val name: String,
    val repository: String,
    val type: String,
    val updated: LocalDateTime,
    val versions: Long
)
