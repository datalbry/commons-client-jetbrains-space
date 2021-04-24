package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.absence.AbsenceClientImpl
import io.datalbry.jetbrains.space.client.blog.BlogsClientImpl
import io.datalbry.jetbrains.space.client.profile.ProfilesClientImpl
import io.datalbry.jetbrains.space.client.projects.ProjectsClientImpl
import io.ktor.client.*
import space.jetbrains.api.runtime.SpaceHttpClient
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.withServiceAccountTokenSource

class JetbrainsSpaceClientFactory {

    companion object {
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