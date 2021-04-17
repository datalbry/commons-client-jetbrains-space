package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.blog.BlogsClientImpl
import io.datalbry.jetbrains.space.client.profile.ProfilesClientImpl
import io.ktor.client.*
import space.jetbrains.api.runtime.SpaceHttpClient
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.withServiceAccountTokenSource

class JetbrainsSpaceClientFactory {

    companion object {
        fun create(config: SpaceConfig): JetbrainsSpaceClient {
            val space = config.toSpaceHttpClientWithCallContext()
            val profiles = ProfilesClientImpl(space)
            val blogs = BlogsClientImpl(space)
            return JetbrainsSpaceClient(profiles, blogs)
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