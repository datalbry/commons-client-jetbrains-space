package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.blog.BlogsClient
import io.datalbry.jetbrains.space.client.profile.ProfilesClient

class JetbrainsSpaceClient internal constructor(
    profiles: ProfilesClient,
    blogs: BlogsClient
) : ProfilesClient by profiles,
    BlogsClient by blogs