package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.models.Profile
import space.jetbrains.api.runtime.types.ProfileIdentifier


interface ProfilesClient {

    fun getProfile(profileIdentifier: ProfileIdentifier): Profile

    fun getProfileIdentifier(): Iterator<ProfileIdentifier>
}