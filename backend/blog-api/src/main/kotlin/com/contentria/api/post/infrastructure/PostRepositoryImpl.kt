package com.contentria.api.post.infrastructure

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostRepository
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.domain.query.CategoryPostCount
import com.contentria.api.post.domain.query.PostSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostRepositoryImpl(
    private val postJpaRepository: PostJpaRepository
) : PostRepository {

    override fun findById(id: UUID): Post? {
        return postJpaRepository.findByIdOrNull(id)
    }

    override fun save(post: Post): Post {
        return postJpaRepository.save(post)
    }

    override fun saveAll(posts: List<Post>): List<Post> {
        return postJpaRepository.saveAll(posts)
    }

    override fun delete(post: Post) {
        postJpaRepository.delete(post)
    }

    override fun deleteAll(posts: List<Post>) {
        postJpaRepository.deleteAll(posts)
    }

    override fun findPostCountsByBlog(blogId: UUID): List<CategoryPostCount> {
        return postJpaRepository.findPostCountsByBlogId(blogId)
    }

    override fun findPostSummariesByBlogSlug(
        blogSlug: String,
        pageable: Pageable
    ): Page<PostSummary> {
        return postJpaRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
    }

    override fun findPublishedPost(
        blogSlug: String,
        postSlug: String
    ): Post? {
        return postJpaRepository.findPublishedPost(blogSlug, postSlug)
    }

    override fun findSlugsByPrefix(
        blogId: UUID,
        targetSlug: String
    ): List<String> {
        return postJpaRepository.findSlugsByPrefix(blogId, targetSlug)
    }

    override fun existsByCategoryIdIn(categoryIds: List<UUID>): Boolean {
        return postJpaRepository.existsByCategoryIdIn(categoryIds)
    }

    override fun countByBlogIdAndStatus(
        blogId: UUID,
        published: PostStatus
    ): Long {
        return postJpaRepository.countByBlogIdAndStatus(blogId, published)
    }
}