package io.datalbry.jetbrains.space.client

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import space.jetbrains.api.runtime.types.ProfileIdentifier

internal class SpaceClientTest {

    private val client = ProfilesClientImpl(loadConfig())

    @Test
    fun getProfile_worksJustFineOnSmallSubset() {
        assertDoesNotThrow {
            client.getProfileIdentifier().asSequence().take(10).forEach {
                client.getProfile(it)
            }
        }
    }

    @Test
    fun getAllProfiles_worksJustFine() {
        assertDoesNotThrow {
            client.getProfileIdentifier()
        }
    }
}

private fun loadConfig(): SpaceConfig {
    return SpaceConfig(
        System.getenv("jetbrains-space-client-id"),
        System.getenv("jetbrains-space-client-secret"),
        System.getenv("jetbrains-space-client-uri"),
    )
}
