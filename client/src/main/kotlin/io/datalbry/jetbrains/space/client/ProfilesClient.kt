package io.datalbry.jetbrains.space.client

import space.jetbrains.api.runtime.types.ProfileIdentifier
import space.jetbrains.api.runtime.types.TD_MemberProfile

interface ProfilesClient {

    fun getProfile(profileIdentifier: ProfileIdentifier): TD_MemberProfile

    fun getProfileIdentifier(): Iterator<ProfileIdentifier>
}