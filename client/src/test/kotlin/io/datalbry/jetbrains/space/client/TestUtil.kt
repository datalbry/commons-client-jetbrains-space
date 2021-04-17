package io.datalbry.jetbrains.space.client

internal fun loadConfig(): SpaceConfig {
    return SpaceConfig(
        System.getenv("jetbrains-space-client-id"),
        System.getenv("jetbrains-space-client-secret"),
        System.getenv("jetbrains-space-client-uri"),
    )
}