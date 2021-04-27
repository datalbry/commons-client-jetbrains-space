package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import io.datalbry.jetbrains.space.models.project.IssueIdentifier
import io.datalbry.jetbrains.space.models.project.Project
import io.datalbry.jetbrains.space.models.project.ProjectIdentifier
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.projects
import space.jetbrains.api.runtime.types.Checklist
import space.jetbrains.api.runtime.types.Issue
import space.jetbrains.api.runtime.types.IssuesSorting
import space.jetbrains.api.runtime.types.PR_Project

class ProjectsClientImpl(private val spaceClient: SpaceHttpClientWithCallContext) : ProjectsClient {

    override fun getProject(projectIdentifier: ProjectIdentifier): Project {
        val tdProject = runBlocking {
            spaceClient.projects.getProject(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id)
            )
        }
        with(tdProject) {
            return Project(
                id = id,
                archived = archived,
                description = description,
                icon = icon,
                projectKey = key.key,
                latestRepositoryActivity = latestRepositoryActivity?.toLocalDateTime(TimeZone.UTC)
                    ?.toJavaLocalDateTime(),
                name = name,
                private = private
            )
        }
    }

    override fun getProjectIdentifier(): Iterator<ProjectIdentifier> {
        return PaginationIterator({ getNextProjectBatch(it) }, { ProjectIdentifier(it.id) })
    }

    override fun getIssue(
        issueIdentifier: IssueIdentifier,
        projectIdentifier: ProjectIdentifier
    ): io.datalbry.jetbrains.space.models.project.Issue {
        val spaceIssue: space.jetbrains.api.runtime.types.Issue = runBlocking {
            spaceClient.projects.planning.issues.getIssueByNumber(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id),
                number = issueIdentifier.number
            )
        }
        with(spaceIssue) {
            return io.datalbry.jetbrains.space.models.project.Issue(
                id = id,
                archived = archived,
                assignee = if (assignee != null) ProfileIdentifier(assignee!!.id) else null,
                attachmentsCount = attachmentsCount,
                createdBy = createdBy.name,
                creationTime = creationTime.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime(),
                dueDate = dueDate?.toJavaLocalDate(),
                number = number,
                projectId = ProjectIdentifier(projectId),

                title = title,
            )
        }
    }

    override fun getIssueIdentifier(projectIdentifier: ProjectIdentifier): Iterator<IssueIdentifier> {
        return PaginationIterator(
            { getNextIssueBatch(it, projectIdentifier) },
            { IssueIdentifier(it.id, it.number) }
        )
    }

    override fun getChecklistIdentifier(projectIdentifier: ProjectIdentifier): Iterator<io.datalbry.jetbrains.space.models.project.Checklist> {
        return PaginationIterator(
            { getNextChecklistBatch(it, projectIdentifier) },
            { spaceChecklistToDataLbryChecklist(it) }
        )
    }

    private fun getNextProjectBatch(batchInfo: BatchInfo): Batch<PR_Project> {
        return runBlocking {
            spaceClient.projects.getAllProjects(batchInfo = batchInfo) {
                id()
            }
        }
    }

    private fun getNextIssueBatch(batchInfo: BatchInfo, projectIdentifier: ProjectIdentifier): Batch<Issue> {
        return runBlocking {
            spaceClient.projects.planning.issues.getAllIssues(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id),
                assigneeId = emptyList(),
                statuses = emptyList(),
                sorting = IssuesSorting.TITLE,
                descending = false,
                batchInfo = batchInfo
            ) {
                id()
            }
        }
    }

    private fun getNextChecklistBatch(batchInfo: BatchInfo, projectIdentifier: ProjectIdentifier): Batch<Checklist> {
        return runBlocking {
            spaceClient.projects.planning.checklists.getAllChecklists(
                batchInfo = batchInfo,
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id)
            )
        }
    }

    companion object {
        private fun spaceChecklistToDataLbryChecklist(spaceChecklist: Checklist): io.datalbry.jetbrains.space.models.project.Checklist {
            return io.datalbry.jetbrains.space.models.project.Checklist(
                id = spaceChecklist.id,
                archived = spaceChecklist.archived,
                description = spaceChecklist.description,
                doneItemsCount = spaceChecklist.doneItemsCount,
                name = spaceChecklist.name,
                owner = if (spaceChecklist.owner != null) ProfileIdentifier(spaceChecklist.owner!!.id) else null,
                project = if (spaceChecklist.projectId != null) ProjectIdentifier(spaceChecklist.projectId!!) else null,
                root = spaceChecklist.root?.id,
                rootTag = spaceChecklist.rootTag?.id,
                totalItemsCount = spaceChecklist.totalItemsCount,
                updatedTime = spaceChecklist.updatedTime?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime()
            )
        }
    }
}