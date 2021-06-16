package io.datalbry.jetbrains.space.client

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class AbsencesClientImplTest {

    private val client = JetbrainsSpaceClientFactory.create(loadConfig())

    @Test
    fun getAbsence_worksJustFineOnSmallSubset() {
        assertDoesNotThrow {
            val elements = client.getAbsenceIdentifier().asSequence().take(10).toList()
            elements.forEach {
                client.getAbsence(it)
            }
        }
    }

    @Test
    fun getAllProfiles_worksJustFine() {
        assertDoesNotThrow {
            client.getAbsenceIdentifier()
        }
    }
}


