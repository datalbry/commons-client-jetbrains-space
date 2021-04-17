package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.absence.AbsenceClient
import io.datalbry.jetbrains.space.client.blog.BlogsClient
import io.datalbry.jetbrains.space.client.profile.ProfilesClient

class JetbrainsSpaceClient internal constructor(
    absences: AbsenceClient,
    blogs: BlogsClient,
    profiles: ProfilesClient,

) : AbsenceClient by absences,
    BlogsClient by blogs,
    ProfilesClient by profiles
