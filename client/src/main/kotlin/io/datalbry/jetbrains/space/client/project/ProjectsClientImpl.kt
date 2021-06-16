package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.client.toJavaLocalDateTime
import io.datalbry.jetbrains.space.client.toLocalDateTime
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import io.datalbry.jetbrains.space.models.project.*
import io.datalbry.jetbrains.space.models.project.IssueIdentifier
import io.datalbry.jetbrains.space.models.project.ProjectIdentifier
import io.datalbry.jetbrains.space.models.project.codereview.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaLocalDate
import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.projects
import space.jetbrains.api.runtime.types.*
import space.jetbrains.api.runtime.types.Checklist
import space.jetbrains.api.runtime.types.Issue
import java.time.Instant
import java.time.LocalDateTime

class ProjectsClientImpl(private val spaceClient: SpaceHttpClientWithCallContext) : ProjectsClient {

    override fun getProject(projectIdentifier: ProjectIdentifier): Project {
        val tdProject = runBlocking {
            spaceClient.projects.getProject(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(projectIdentifier.key)
            )
        }
        with(tdProject) {
            return Project(
                id = id,
                archived = archived,
                description = description,
                icon = icon,
                projectKey = key.key,
                latestRepositoryActivity = latestRepositoryActivity?.toJavaLocalDateTime(),
                name = name,
                private = private
            )
        }
    }

    override fun getProjectIdentifier(): Iterator<ProjectIdentifier> {
        return PaginationIterator({ getNextProjectBatch(it) }, { ProjectIdentifier(it.key.key) })
    }

    override fun getIssue(issueIdentifier: IssueIdentifier): io.datalbry.jetbrains.space.models.project.Issue {
        val spaceIssue: space.jetbrains.api.runtime.types.Issue = runBlocking {
            spaceClient.projects.planning.issues.getIssueByNumber(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(issueIdentifier.projectKey),
                number = issueIdentifier.issueNumber
            )
        }
        with(spaceIssue) {
            return io.datalbry.jetbrains.space.models.project.Issue(
                id = id,
                archived = archived,
                assignee = if (assignee != null) ProfileIdentifier(assignee!!.id) else null,
                attachmentsCount = attachmentsCount,
                createdBy = createdBy.name,
                creationTime = creationTime.toJavaLocalDateTime(),
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
            { IssueIdentifier(issueId = it.id, projectKey = projectIdentifier.key, issueNumber = it.number) }
        )
    }

    override fun getChecklists(projectIdentifier: ProjectIdentifier): Iterator<io.datalbry.jetbrains.space.models.project.Checklist> {
        return PaginationIterator(
            { getNextChecklistBatch(it, projectIdentifier) },
            Checklist::toDataLbryChecklist
        )
    }

    override fun getCodeReview(codeReviewIdentifier: CodeReviewIdentifier): CodeReview? {
        val spaceCodeReview: CodeReviewRecord? = runBlocking {
            spaceClient.projects.codeReviews.getCodeReview(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(codeReviewIdentifier.projectKey),
                reviewId = ReviewIdentifier.Id(codeReviewIdentifier.reviewId)
            )
        }

        return when (spaceCodeReview) {
            is MergeRequestRecord -> spaceCodeReview.toDataLbryMergeReview()
            is CommitSetReviewRecord -> spaceCodeReview.toToDataLbryCodeReview()
            else -> null
        }

    }

    override fun getCodeReviewIdentifier(projectIdentifier: ProjectIdentifier): Iterator<CodeReviewIdentifier> {
        return PaginationIterator(
            { getNextCodeReviewBatch(it, projectIdentifier) },
            { CodeReviewIdentifier(projectKey = projectIdentifier.key, reviewId = it.review.id) }
        )
    }

    override fun getRepository(repositoryIdentifier: RepositoryIdentifier): Repository? {
        val spaceRepository = runBlocking {
            spaceClient.projects.packages.repositories.getRepository(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(repositoryIdentifier.projectKey),
                repository = PackageRepositoryIdentifier.Id(repositoryIdentifier.repositoryId)
            )
        }

        if (spaceRepository == null) {
            return spaceRepository
        }

        return Repository(
            id = spaceRepository.id,
            archived = spaceRepository.archived,
            description = spaceRepository.description,
            name = spaceRepository.name,
            project = ProjectIdentifier(spaceRepository.project.id),
            packageRepositoryId = spaceRepository.repository.id,
        )
    }

    override fun getRepositoryIdentifier(projectIdentifier: ProjectIdentifier): Iterator<RepositoryIdentifier> {
        // this already returns a list and not a batch object hence we don't need a PaginationIterator like in the
        // other methods
        return runBlocking {
            spaceClient.projects.packages.repositories.getRepositories(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(projectIdentifier.key),
            ) {
                id()
            }
        }.map { RepositoryIdentifier(projectKey = projectIdentifier.key, repositoryId = it.id) }.iterator()
    }

    fun getPackages(repositoryIdentifier: RepositoryIdentifier): Iterator<Package> {
        return PaginationIterator(
            { getNextPackageBatch(it, repositoryIdentifier) },
            { spacePackageDataToDataLbryPackageData(it) }
        )
    }

    private fun getNextProjectBatch(batchInfo: BatchInfo): Batch<PR_Project> {
        return runBlocking {
            spaceClient.projects.getAllProjects(batchInfo = batchInfo) {
                key()
            }
        }
    }

    private fun getNextIssueBatch(batchInfo: BatchInfo, projectIdentifier: ProjectIdentifier): Batch<Issue> {
        return runBlocking {
            spaceClient.projects.planning.issues.getAllIssues(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(projectIdentifier.key),
                assigneeId = emptyList(),
                statuses = emptyList(),
                sorting = IssuesSorting.TITLE,
                descending = false,
                batchInfo = batchInfo
            ) {
                id()
                number()
            }
        }
    }

    private fun getNextChecklistBatch(batchInfo: BatchInfo, projectIdentifier: ProjectIdentifier): Batch<Checklist> {
        return runBlocking {
            spaceClient.projects.planning.checklists.getAllChecklists(
                batchInfo = batchInfo,
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(projectIdentifier.key)
            )
        }
    }

    private fun getNextCodeReviewBatch(
        batchInfo: BatchInfo,
        projectIdentifier: ProjectIdentifier
    ): Batch<CodeReviewWithCount> {
        return runBlocking {
            spaceClient.projects.codeReviews.getAllCodeReviews(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(projectIdentifier.key),
                batchInfo = batchInfo,
            ) {
                review {
                    id()
                }
            }
        }
    }

    private fun getNextPackageBatch(
        batchInfo: BatchInfo,
        repositoryIdentifier: RepositoryIdentifier
    ): Batch<space.jetbrains.api.runtime.types.PackageData> {
        return runBlocking {
            spaceClient.projects.packages.repositories.packages.getAllPackages(
                batchInfo = batchInfo,
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Key(repositoryIdentifier.projectKey),
                repository = PackageRepositoryIdentifier.Id(repositoryIdentifier.repositoryId),
                query = ""
            )
        }
    }

    companion object {
        private fun spacePackageDataToDataLbryPackageData(spacePackageData: PackageData): Package {
            val type = when (spacePackageData.type) {
                is ContainerPackageType -> (spacePackageData.type as ContainerPackageType).id
                is MavenPackageType -> (spacePackageData.type as MavenPackageType).id
                is NpmPackageType -> (spacePackageData.type as NpmPackageType).id
                is NuGetPackageType -> (spacePackageData.type as NuGetPackageType).id
                else -> ""
            }
            return Package(
                lastVersion = spacePackageData.lastVersion,
                name = spacePackageData.name,
                repository = spacePackageData.repository,
                type = type,
                updated = LocalDateTime.from(Instant.ofEpochMilli(spacePackageData.updated)),
                versions = spacePackageData.versions
            )
        }
    }
}

internal fun CommitSetReviewRecord.toToDataLbryCodeReview() = CommitSetCodeReview(
    id = id,
    canBeReopened = canBeReopened,
    createdAt = Instant.ofEpochMilli(createdAt).toLocalDateTime(),
    createdBy = if (createdBy?.id != null) ProfileIdentifier(createdBy!!.id) else null,
    number = number,
    projectKey = project.key,
    projectIdentifier = ProjectIdentifier(projectId),
    state = state.name,
    title = title,
    turnBased = turnBased
)

internal fun MergeRequestBranch.toDataLbryBranchInformation() =
    BranchInformation(isDeleted = deleted, displayName = displayName, head = head, reference = ref)

internal fun MergeRequestBranchPair.toDataLbryBranchMergePair() = BranchMergePair(
    isMerged = isMerged,
    repository = repository,
    source = sourceBranchInfo?.toDataLbryBranchInformation(),
    target = targetBranchInfo?.toDataLbryBranchInformation(),
)

internal fun MergeRequestRecord.toDataLbryMergeReview() = MergeRequestCodeReview(
    id = id,
    canBeReopened = canBeReopened,
    createdAt = Instant.ofEpochMilli(createdAt).toLocalDateTime(),
    createdBy = if (createdBy?.id != null) ProfileIdentifier(createdBy!!.id) else null,
    number = number,
    projectKey = project.key,
    projectIdentifier = ProjectIdentifier(projectId),
    state = state.name,
    title = title,
    turnBased = turnBased,
    branchInformation = branchPairs.map(MergeRequestBranchPair::toDataLbryBranchMergePair).toList()
)

internal fun Checklist.toDataLbryChecklist() = io.datalbry.jetbrains.space.models.project.Checklist(
    id = id,
    archived = archived,
    description = description,
    doneItemsCount = doneItemsCount,
    name = name,
    owner = if (owner != null) ProfileIdentifier(owner!!.id) else null,
    project = if (projectId != null) ProjectIdentifier(projectId!!) else null,
    root = root?.id,
    rootTag = rootTag?.id,
    totalItemsCount = totalItemsCount,
    updatedTime = updatedTime?.toJavaLocalDateTime()
)

