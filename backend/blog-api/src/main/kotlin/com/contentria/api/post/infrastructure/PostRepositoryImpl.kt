package com.contentria.api.post.infrastructure

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostRepository
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.domain.query.CategoryPostCount
import com.contentria.api.post.domain.query.PostSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostRepositoryImpl(
    private val postJpaRepository: PostJpaRepository,
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

    override fun deleteById(id: UUID) {
        postJpaRepository.deleteById(id)
    }

    override fun findPostCountsByBlog(blogId: UUID): List<CategoryPostCount> {
        return postJpaRepository.findPostCountsByBlogId(blogId)
    }

    override fun findPostSummaries(
        blogSlug: String,
        categoryIds: List<UUID>?,
        statuses: Set<PostStatus>,
        pageable: Pageable
    ): Page<PostSummary> {
        val mixedSort = JpaSort.unsafe(Sort.Direction.DESC, "COALESCE(p.publishedAt, p.createdAt)")
        val customPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, mixedSort)
        return postJpaRepository.findPostSummaries(blogSlug, categoryIds, statuses, customPageable)
    }

    override fun findPublishedPost(
        blogSlug: String,
        postSlug: String
    ): Post? {
        return postJpaRepository.findPublishedPost(blogSlug, postSlug)
    }

    override fun findPublishedPost(postId: UUID): Post? {
        return postJpaRepository.findByIdAndStatus(postId, PostStatus.PUBLISHED)
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