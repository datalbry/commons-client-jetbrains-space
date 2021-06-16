package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.models.project.*
import io.datalbry.jetbrains.space.models.project.codereview.CodeReview
import io.datalbry.jetbrains.space.models.project.codereview.CodeReviewIdentifier

/**
 * The ProjectClient interface provides all needed endpoints to retrieve data for your projects from JetBrains Space.
 *
 * Currently supported is:
 *     <ul>
 *      <li>Project</li>
 *      <li>Issue</li>
 *      <li>Checklists</li>
 *      <li>CodeReview</li>
 *      <li>Repository</li>
 *     </ul>
 */
interface ProjectsClient {

    /**
     * Retrieves a project from JetBrains Space given a ProjectIdentifier
     *
     * @param projectIdentifier: An identifier for a project in JetBrains Space
     * @return A project
     */
    fun getProject(projectIdentifier: ProjectIdentifier): Project

    /**
     * Retrieves from JetBrains Space ProjectIdentifier
     *
     * @returns An iterator over all available projects
     */
    fun getProjectIdentifier(): Iterator<ProjectIdentifier>

    /**
     * Retrieves an Issue from JetBrains Space given an IssueIdentifier
     *
     * @param issueIdentifier: An identifier for an Issue in JetBrains Space
     * @return An Issue
     */
    fun getIssue(issueIdentifier: IssueIdentifier): Issue

    /**
     * Retrieves from JetBrains Space IssueIdentifier in a project
     *
     * @param The target project
     * @returns An iterator over all available issues in a project
     */
    fun getIssueIdentifier(projectIdentifier: ProjectIdentifier): Iterator<IssueIdentifier>

    /**
     * Retrieves all Checklists in a Project
     *
     * @param projectIdentifier: An identifier for a project in JetBrains Space
     * @return An Iterator of Checklists
     */
    fun getChecklists(projectIdentifier: ProjectIdentifier): Iterator<Checklist>

    /**
     * Retrieves an CodeReview from JetBrains Space given an CodeReviewIdentifier
     *
     * @param codeReviewIdentifier: An identifier for a code review
     * @return An instance of an code review
     */
    fun getCodeReview(codeReviewIdentifier: CodeReviewIdentifier): CodeReview?

    /**
     * Retrieves from JetBrains Space code review identifier in a project
     *
     * @param The target project
     * @returns An iterator over all available CodeReviewIdentifier in a project
     */
    fun getCodeReviewIdentifier(projectIdentifier: ProjectIdentifier): Iterator<CodeReviewIdentifier>

    fun getRepository(repositoryIdentifier: RepositoryIdentifier): Repository?
    fun getRepositoryIdentifier(projectIdentifier: ProjectIdentifier): Iterator<RepositoryIdentifier>
}
