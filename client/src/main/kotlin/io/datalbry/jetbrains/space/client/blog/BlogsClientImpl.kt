package io.datalbry.jetbrains.space.client.blog


import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.models.Blog
import io.datalbry.jetbrains.space.models.BlogIdentifier
import io.datalbry.jetbrains.space.models.ProfileIdentifier
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import space.jetbrains.api.runtime.Batch
import space.jetbrains.api.runtime.BatchInfo
import space.jetbrains.api.runtime.SpaceHttpClientWithCallContext
import space.jetbrains.api.runtime.resources.blog
import space.jetbrains.api.runtime.types.ArticleRecord

class BlogsClientImpl(private val space: SpaceHttpClientWithCallContext) : BlogsClient {

    override fun getBlog(identifier: BlogIdentifier): Blog {
        val articleRecord = runBlocking {
            space.blog.getBlogPost(
                id = identifier.id
            )
        }

        with(articleRecord) {
            return Blog(
                id = id,
                archived = archived,
                archivedAt = archivedAt?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime(),
                archivedBy = if (archivedBy != null) ProfileIdentifier(archivedBy!!.id) else null,
                author = ProfileIdentifier(author.id),
                created = created.toLocalDateTime(TimeZone.UTC).toJavaLocalDateTime(),
                title = title
            )
        }

    }

    override fun getBlogIdentifier(): Iterator<BlogIdentifier> {
        return PaginationIterator({ getNextBatch(it) }, { BlogIdentifier(it.id) })
    }

    private fun getNextBatch(batchInfo: BatchInfo): Batch<ArticleRecord> {
        return runBlocking {
            space.blog.getAllBlogPosts(batchInfo = batchInfo) {
                id()
            }
        }
    }
}