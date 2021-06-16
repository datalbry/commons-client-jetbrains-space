package io.datalbry.jetbrains.space.client

internal fun loadConfig(): SpaceConfig {
    return SpaceConfig(
        System.getenv("JETBRAINS_SPACE_CLIENT_ID"),
        System.getenv("JETBRAINS_SPACE_CLIENT_SECRET"),
        System.getenv("JETBRAINS_SPACE_CLIENT_URI"),
    )
}
