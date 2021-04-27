package io.datalbry.jetbrains.space.client.profile

import io.datalbry.jetbrains.space.models.profile.Profile
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier


interface ProfilesClient {

    fun getProfile(profileIdentifier: ProfileIdentifier): Profile

    fun getProfileIdentifier(): Iterator<ProfileIdentifier>


}