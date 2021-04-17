package io.datalbry.jetbrains.space.client.blog

import io.datalbry.jetbrains.space.models.Blog
import io.datalbry.jetbrains.space.models.BlogIdentifier


interface BlogsClient {

    fun getBlog(identifier: BlogIdentifier): Blog

    fun getBlogIdentifier(): Iterator<BlogIdentifier>
}