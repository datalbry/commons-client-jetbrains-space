package io.datalbry.jetbrains.space.models.profile

import java.time.LocalDateTime


data class Profile(
    val id: String,
    val archived: Boolean,
    val avatar: String?,
    val joined: LocalDateTime?,
    val left: LocalDateTime?,
    val leftAt: LocalDateTime?,
    val firstName: String,
    val lastName: String,
    val notAMember: Boolean,
    val profilePicture: String?,
    val smallAvatar: String?,
    val speaksEnglish: Boolean,
    val username: String,
)