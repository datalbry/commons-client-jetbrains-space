package io.datalbry.jetbrains.space.client

import kotlinx.datetime.toJavaInstant
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

internal fun Instant.toLocalDateTime() = LocalDateTime.ofInstant(this, ZoneOffset.UTC)

internal fun kotlinx.datetime.Instant.toJavaLocalDateTime() = this.toJavaInstant().toLocalDateTime()