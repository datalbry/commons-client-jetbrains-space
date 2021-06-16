package io.datalbry.jetbrains.space.client

/**
 * The data required to authenticate to JetBrains Space
 */
data class SpaceConfig(
    val clientId: String,
    val clientSecret: String,
    val serverUrl: String,
)
