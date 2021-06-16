package io.datalbry.jetbrains.space.client

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class ProjectsClientImplTest {

    private val client = JetbrainsSpaceClientFactory.create(loadConfig())

    @Test
    fun getProjectIdentifier_noDuplicates() {
        val projectIdentifier = client.getProjectIdentifier()
            .asSequence()
            .toList()
            .map {it.key}

        assert(projectIdentifier.size == projectIdentifier.toSet().size)

    }
}


