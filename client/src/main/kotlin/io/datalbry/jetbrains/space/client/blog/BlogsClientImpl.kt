package io.datalbry.jetbrains.space.client.blog


import io.datalbry.jetbrains.space.client.PaginationIterator
import io.datalbry.jetbrains.space.client.toJavaLocalDateTime
import io.datalbry.jetbrains.space.models.blog.Blog
import io.datalbry.jetbrains.space.models.blog.BlogIdentifier
import io.datalbry.jetbrains.space.models.profile.ProfileIdentifier
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

        return articleRecord.toBlog()
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

internal fun ArticleRecord.toBlog() = Blog(id = id,
    archived = archived,
    archivedAt = archivedAt?.toJavaLocalDateTime(),
    archivedBy = if (archivedBy != null) ProfileIdentifier(archivedBy!!.id) else null,
    author = ProfileIdentifier(author.id),
    created = created.toJavaLocalDateTime(),
    title = title
)