package io.datalbry.jetbrains.space.client.profile

import io.datalbry.jetbrains.space.models.profile.Profile
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier

/**
 * The ProfilesClient interface provides all needed endpoints to retrieve data about Profiles in your JetBrains Space
 */
interface ProfilesClient {

    /**
     * Given an identifier retrieves an Profile
     *
     * @param profileIdentifier: An Profile identifier
     * @return A Profile object
     */
    fun getProfile(profileIdentifier: ProfileIdentifier): Profile

    /**
     * Retrives all profile identifier from the JetBrains Space
     *
     * @return All profile identifiers in the given JetBrains Space in form of an iterator
     */
    fun getProfileIdentifier(): Iterator<ProfileIdentifier>


}