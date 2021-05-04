package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.models.project.*

interface ProjectsClient {

    fun getProject(projectIdentifier: ProjectIdentifier): Project

    fun getProjectIdentifier(): Iterator<ProjectIdentifier>

    fun getIssue(issueIdentifier: IssueIdentifier): Issue

    fun getIssueIdentifier(projectIdentifier: ProjectIdentifier): Iterator<IssueIdentifier>

    fun getChecklists(projectIdentifier: ProjectIdentifier): Iterator<Checklist>
    fun getCodeReview(codeReviewIdentifier: CodeReviewIdentifier): CodeReview?
    fun getCodeReviewIdentifier(projectIdentifier: ProjectIdentifier): Iterator<CodeReviewIdentifier>
}