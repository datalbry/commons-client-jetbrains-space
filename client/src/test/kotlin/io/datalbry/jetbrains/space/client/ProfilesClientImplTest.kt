package io.datalbry.jetbrains.space.client

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class SpaceClientTest {

    private val client = JetbrainsSpaceClientFactory.create(loadConfig())

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


