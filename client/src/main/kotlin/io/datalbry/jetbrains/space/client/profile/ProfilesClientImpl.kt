package io.datalbry.jetbrains.space.client.profile

import io.datalbry.jetbrains.space.client.BatchIterator
import io.datalbry.jetbrains.space.models.Profile
import io.datalbry.jetbrains.space.models.ProfileIdentifier
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.teamDirectory
import space.jetbrains.api.runtime.types.TD_MemberProfile

class ProfilesClientImpl(private val spaceClient: SpaceHttpClientWithCallContext) : ProfilesClient {

    override fun getProfile(profileIdentifier: io.datalbry.jetbrains.space.models.ProfileIdentifier): Profile {
        val profile: TD_MemberProfile = runBlocking {
            spaceClient.teamDirectory.profiles.getProfile(
                profile = space.jetbrains.api.runtime.types.ProfileIdentifier.Id(profileIdentifier.key)
            )
        }

        return Profile(
            id = profile.id,
            archived = profile.archived,
            avatar = profile.avatar,
            joined = profile.joined?.toJavaLocalDate()?.atStartOfDay(),
            left = profile.left?.toJavaLocalDate()?.atStartOfDay(),
            leftAt = profile.leftAt?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime(),
            firstName = profile.name.firstName,
            lastName = profile.name.lastName,
            notAMember = profile.notAMember,
            profilePicture = profile.profilePicture,
            smallAvatar = profile.smallAvatar,
            speaksEnglish = profile.speaksEnglish,
            username = profile.username,
        )
    }

    override fun getProfileIdentifier(): Iterator<io.datalbry.jetbrains.space.models.ProfileIdentifier> {
        fun getNextBatch(batchInfo: BatchInfo): Batch<TD_MemberProfile> {
            return runBlocking {
                spaceClient.teamDirectory.profiles.getAllProfiles() {
                    id()
                }
            }
        }

        return BatchIterator.from(::getNextBatch) { io.datalbry.jetbrains.space.models.ProfileIdentifier(it.id) }
    }
}