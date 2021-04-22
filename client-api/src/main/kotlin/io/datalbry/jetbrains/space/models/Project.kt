package io.datalbry.jetbrains.space.models

import java.time.LocalDateTime

data class Project(
    val id : String,
    val archived : Boolean,
    val description : String?,
    val icon : String?,
    val projectKey : String,
    val latestRepositoryActivity : LocalDateTime?,
    val name : String,
    val private : Boolean,
)