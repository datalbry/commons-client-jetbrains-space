package io.datalbry.jetbrains.space.client

import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import space.jetbrains.api.runtime.SpaceHttpClient
import space.jetbrains.api.runtime.resources.teamDirectory
import space.jetbrains.api.runtime.types.ProfileIdentifier
import space.jetbrains.api.runtime.types.TD_MemberProfile
import space.jetbrains.api.runtime.withServiceAccountTokenSource

class ProfilesClientImpl(config: SpaceConfig) : ProfilesClient {

    private val space = SpaceHttpClient(HttpClient()).withServiceAccountTokenSource(
        clientId = config.clientId,
        clientSecret = config.clientSecret,
        serverUrl = config.serverUrl
    )

    override fun getProfile(profileIdentifier: ProfileIdentifier): TD_MemberProfile {
        return runBlocking {
            space.teamDirectory.profiles.getProfile(profile = profileIdentifier)
        }
    }

    override fun getProfileIdentifier(): Iterator<ProfileIdentifier> {
        return runBlocking {
            space.teamDirectory.profiles.getAllProfiles() {
                id()
            }.data.map { ProfileIdentifier.Id(it.id) }.iterator()
        }
    }
}