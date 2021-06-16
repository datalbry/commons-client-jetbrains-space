package io.datalbry.jetbrains.space.client

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class BlogsClientImplTest {
    private val client = JetbrainsSpaceClientFactory.create(loadConfig())


    @Test
    fun getBlog_worksJustFineOnSmallSubset() {
        assertDoesNotThrow {
            client.getBlogIdentifier().forEach { client.getBlog(it) }
        }
    }

    @Test
    fun getAllBlogs_worksJustFine() {
        assertDoesNotThrow {
            runBlocking {
                client.getBlogIdentifier()
            }
        }
    }
}