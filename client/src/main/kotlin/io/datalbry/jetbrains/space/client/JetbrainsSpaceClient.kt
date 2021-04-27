package io.datalbry.jetbrains.space.client

import io.datalbry.jetbrains.space.client.absence.AbsenceClient
import io.datalbry.jetbrains.space.client.blog.BlogsClient
import io.datalbry.jetbrains.space.client.profile.ProfilesClient
import io.datalbry.jetbrains.space.client.project.ProjectsClient

class JetbrainsSpaceClient internal constructor(
    absences: AbsenceClient,
    blogs: BlogsClient,
    profiles: ProfilesClient,
    projects: ProjectsClient

) : AbsenceClient by absences,
    BlogsClient by blogs,
    ProfilesClient by profiles,
    ProjectsClient by projects
