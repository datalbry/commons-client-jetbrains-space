package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.absence.AbsenceClientImpl
import io.datalbry.jetbrains.space.client.blog.BlogsClientImpl
import io.datalbry.jetbrains.space.client.profile.ProfilesClientImpl
import io.datalbry.jetbrains.space.client.project.ProjectsClientImpl
import io.ktor.client.*
import space.jetbrains.api.runtime.SpaceHttpClient
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.withServiceAccountTokenSource


/**
 * JetBrainsSpaceClientFactory produces an instance of a JetBrainsSpaceClient given the current configuration.
 */
class JetbrainsSpaceClientFactory {

    companion object {
        /**
         * Produces an instance of a JetBrainsSpaceClient given the current configuration.
         *
         * @param config: containing the credentials to authorize towards JetBrains Space
         * @return A JetBrainsSpaceClient
         */
        fun create(config: SpaceConfig): JetbrainsSpaceClient {
            val space = config.toSpaceHttpClientWithCallContext()
            return JetbrainsSpaceClient(
                absences=AbsenceClientImpl(space),
                blogs=BlogsClientImpl(space),
                profiles=ProfilesClientImpl(space),
                projects=ProjectsClientImpl(space),
            )
        }
    }

}

private fun SpaceConfig.toSpaceHttpClientWithCallContext(): SpaceHttpClientWithCallContext {
    return SpaceHttpClient(HttpClient()).withServiceAccountTokenSource(
        clientId = clientId,
        clientSecret = clientSecret,
        serverUrl = serverUrl
    )
}