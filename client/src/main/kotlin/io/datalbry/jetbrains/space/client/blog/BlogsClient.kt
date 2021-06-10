package io.datalbry.jetbrains.space.client.blog

import io.datalbry.jetbrains.space.models.blog.Blog
import io.datalbry.jetbrains.space.models.blog.BlogIdentifier

/**
 * The BlogsClient interface provides all needed endpoints to retrieve data Blog Data from JetBrains Space.
 */
interface BlogsClient {

    /**
     * Given an identifier retrieves an Blog entry
     *
     * @param identifier: An blog identifier
     * @return an Blog object
     */
    fun getBlog(identifier: BlogIdentifier): Blog

    /**
     * Retrives all Blog identifier from the JetBrains Space
     *
     * @return All Blog identifiers in the given JetBrains Space in form of an iterator
     */
    fun getBlogIdentifier(): Iterator<BlogIdentifier>
}