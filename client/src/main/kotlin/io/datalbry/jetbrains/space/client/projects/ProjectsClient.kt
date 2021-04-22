package io.datalbry.jetbrains.space.client.projects

import io.datalbry.jetbrains.space.models.*

interface ProjectsClient {

    fun getProject(projectIdentifier: ProjectIdentifier): Project

    fun getProjectIdentifier(): Iterator<ProjectIdentifier>

    fun getIssue(issueIdentifier: IssueIdentifier, projectIdentifier: ProjectIdentifier): Issue

    fun getIssueIdentifier(projectIdentifier: ProjectIdentifier): Iterator<IssueIdentifier>
    fun getChecklistIdentifier(projectIdentifier: ProjectIdentifier): Iterator<ChecklistIdentifier>
}