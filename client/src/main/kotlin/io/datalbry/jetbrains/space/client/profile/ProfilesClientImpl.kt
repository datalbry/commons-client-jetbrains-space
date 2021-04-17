package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.models.Profile
import kotlinx.coroutines.runBlocking
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.teamDirectory
import space.jetbrains.api.runtime.types.ProfileIdentifier
import space.jetbrains.api.runtime.types.TD_MemberProfile

class ProfilesClientImpl(private val space: SpaceHttpClientWithCallContext) : ProfilesClient {

    override fun getProfile(profileIdentifier: ProfileIdentifier): Profile {
        val profile: TD_MemberProfile = runBlocking {
            space.teamDirectory.profiles.getProfile(profile = profileIdentifier)
        }

        return Profile(
            id = profile.id,
            archived = profile.archived,
            avatar = profile.avatar,
            joined = profile.joined,
            left = profile.left,
            leftAt = profile.leftAt,
            firstName = profile.name.firstName,
            lastName = profile.name.lastName,
            notAMember = profile.notAMember,
            profilePicture = profile.profilePicture,
            smallAvatar = profile.smallAvatar,
            speaksEnglish = profile.speaksEnglish,
            username = profile.username,
        )
    }

    override fun getProfileIdentifier(): Iterator<ProfileIdentifier> {
        return runBlocking {
            space.teamDirectory.profiles.getAllProfiles() {
                id()
            }.data.map { ProfileIdentifier.Id(it.id) }.iterator()
        }
    }
}