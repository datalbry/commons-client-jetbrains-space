package io.datalbry.jetbrains.space.client.project

import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
import io.datalbry.jetbrains.space.models.project.*
import io.datalbry.jetbrains.space.models.project.IssueIdentifier
import io.datalbry.jetbrains.space.models.project.ProjectIdentifier
import io.datalbry.jetbrains.space.models.project.codereview.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
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

    override fun getIssue(issueIdentifier: IssueIdentifier): io.datalbry.jetbrains.space.models.project.Issue {
        val spaceIssue: space.jetbrains.api.runtime.types.Issue = runBlocking {
            spaceClient.projects.planning.issues.getIssueByNumber(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(issueIdentifier.projectId),
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
            { IssueIdentifier(projectId = it.id, issueNumber = it.number) }
        )
    }

    override fun getChecklists(projectIdentifier: ProjectIdentifier): Iterator<io.datalbry.jetbrains.space.models.project.Checklist> {
        return PaginationIterator(
            { getNextChecklistBatch(it, projectIdentifier) },
            { spaceChecklistToDataLbryChecklist(it) }
        )
    }

    override fun getCodeReview(codeReviewIdentifier: CodeReviewIdentifier): CodeReview? {
        val spaceCodeReview: CodeReviewRecord? = runBlocking {
            spaceClient.projects.codeReviews.getCodeReview(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(codeReviewIdentifier.projectId),
                reviewId = ReviewIdentifier.Id(codeReviewIdentifier.reviewId)
            )
        }

        return when (spaceCodeReview) {
            is MergeRequestRecord -> spaceMergeReviewToDataLbryMergeReview(spaceCodeReview)
            is CommitSetReviewRecord -> spaceCodeReviewToDataLbryCodeReview(spaceCodeReview)
            else -> null
        }

    }

    override fun getCodeReviewIdentifier(projectIdentifier: ProjectIdentifier): Iterator<CodeReviewIdentifier> {
        return PaginationIterator(
            { getNextCodeReviewBatch(it, projectIdentifier) },
            { CodeReviewIdentifier(projectId = projectIdentifier.id, reviewId = it.review.id) }
        )
    }

    fun getRepository(repositoryIdentifier: RepositoryIdentifier): Repository? {
        val spaceRepository = runBlocking {
            spaceClient.projects.packages.repositories.getRepository(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(repositoryIdentifier.projectId),
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

    fun getRepositoryIdentifier(projectIdentifier: ProjectIdentifier): Iterator<RepositoryIdentifier> {
        // this already returns a list and not a batch object hence we don't need a PaginationIterator like in the
        // other methods
        return runBlocking {
            spaceClient.projects.packages.repositories.getRepositories(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id),
            ) {
                id()
            }
        }.map { RepositoryIdentifier(projectId = projectIdentifier.id, repositoryId = it.id) }.iterator()
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

    private fun getNextCodeReviewBatch(
        batchInfo: BatchInfo,
        projectIdentifier: ProjectIdentifier
    ): Batch<CodeReviewWithCount> {
        return runBlocking {
            spaceClient.projects.codeReviews.getAllCodeReviews(
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(projectIdentifier.id),
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
                project = space.jetbrains.api.runtime.types.ProjectIdentifier.Id(repositoryIdentifier.projectId),
                repository = PackageRepositoryIdentifier.Id(repositoryIdentifier.repositoryId),
                query = ""
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

        private fun spaceBranchToDataLbryBranch(branch: MergeRequestBranch?): BranchInformation? {
            return if (branch != null) {
                BranchInformation(
                    isDeleted = branch.deleted,
                    displayName = branch.displayName,
                    head = branch.head,
                    reference = branch.ref
                )
            } else null
        }

        private fun spaceBranchPairToDataLbryBranchMergePair(mergeRequestBranchPair: MergeRequestBranchPair): BranchMergePair {
            return BranchMergePair(
                isMerged = mergeRequestBranchPair.isMerged,
                repository = mergeRequestBranchPair.repository,
                source = spaceBranchToDataLbryBranch(mergeRequestBranchPair.sourceBranchInfo),
                target = spaceBranchToDataLbryBranch(mergeRequestBranchPair.targetBranchInfo),
            )
        }

        private fun spaceCodeReviewToDataLbryCodeReview(spaceCodeReview: CommitSetReviewRecord): CommitSetCodeReview {
            return CommitSetCodeReview(
                id = spaceCodeReview.id,
                canBeReopened = spaceCodeReview.canBeReopened,
                createdAt = LocalDateTime.from(Instant.ofEpochMilli(spaceCodeReview.createdAt)),
                createdBy = if (spaceCodeReview.createdBy?.id != null) ProfileIdentifier(spaceCodeReview.createdBy!!.id) else null,
                number = spaceCodeReview.number,
                projectKey = spaceCodeReview.project.key,
                projectIdentifier = ProjectIdentifier(spaceCodeReview.projectId),
                state = spaceCodeReview.state.name,
                title = spaceCodeReview.title,
                turnBased = spaceCodeReview.turnBased
            )
        }

        private fun spaceMergeReviewToDataLbryMergeReview(spaceCodeReview: MergeRequestRecord): MergeRequestCodeReview {
            return MergeRequestCodeReview(
                id = spaceCodeReview.id,
                canBeReopened = spaceCodeReview.canBeReopened,
                createdAt = LocalDateTime.from(Instant.ofEpochMilli(spaceCodeReview.createdAt)),
                createdBy = if (spaceCodeReview.createdBy?.id != null) ProfileIdentifier(spaceCodeReview.createdBy!!.id) else null,
                number = spaceCodeReview.number,
                projectKey = spaceCodeReview.project.key,
                projectIdentifier = ProjectIdentifier(spaceCodeReview.projectId),
                state = spaceCodeReview.state.name,
                title = spaceCodeReview.title,
                turnBased = spaceCodeReview.turnBased,
                branchInformation = spaceCodeReview.branchPairs.map {
                    spaceBranchPairToDataLbryBranchMergePair(it)
                }.toList()
            )
        }

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