package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.models.project.*

interface ProjectsClient {

    fun getProject(projectIdentifier: ProjectIdentifier): Project

    fun getProjectIdentifier(): Iterator<ProjectIdentifier>

    fun getIssue(issueIdentifier: IssueIdentifier): Issue

    fun getIssueIdentifier(projectIdentifier: ProjectIdentifier): Iterator<IssueIdentifier>

    fun getChecklistIdentifier(projectIdentifier: ProjectIdentifier): Iterator<ChecklistIdentifier>
}