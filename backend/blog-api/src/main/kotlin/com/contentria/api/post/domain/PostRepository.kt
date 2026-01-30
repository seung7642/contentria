package com.contentria.api.post.domain

import com.contentria.api.post.domain.query.CategoryPostCount
import com.contentria.api.post.domain.query.PostSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface PostRepository {

    fun findById(id: UUID): Post?
    fun save(post: Post): Post
    fun saveAll(posts: List<Post>): List<Post>
    fun delete(post: Post)
    fun deleteAll(posts: List<Post>)

    fun findPostCountsByBlog(blogId: UUID): List<CategoryPostCount>

    fun findPostSummariesByBlogSlug(blogSlug: String, pageable: Pageable): Page<PostSummary>

    fun findPublishedPost(blogSlug: String, postSlug: String): Post?

    fun findSlugsByPrefix(blogId: UUID, targetSlug: String): List<String>

    fun existsByCategoryIdIn(categoryIds: List<UUID>): Boolean

    fun countByBlogIdAndStatus(blogId: UUID, published: PostStatus): Long
}