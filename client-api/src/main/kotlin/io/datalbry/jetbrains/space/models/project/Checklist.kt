package io.datalbry.jetbrains.space.models.project

import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import java.time.LocalDateTime

data class Checklist(
    val id: String,
    val archived: Boolean,
    val description: String?,
    val doneItemsCount: Int,
    val name: String,
    val owner: ProfileIdentifier?,
    val project: ProjectIdentifier?,
    val root: String?, // TOOD this is a PlanItem
    val rootTag: String?, // TODO this is a PlanningTag
    val totalItemsCount: Int,
    val updatedTime: LocalDateTime?,
)
