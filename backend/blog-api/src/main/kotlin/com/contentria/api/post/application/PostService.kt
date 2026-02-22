package com.contentria.api.post.application

import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.post.application.dto.PostContentInfo
import com.contentria.api.post.application.dto.PostSummaryInfo
import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostRepository
import com.contentria.api.post.domain.PostStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class PostService(
    private val postRepository: PostRepository
) {
    @Transactional(readOnly = true)
    fun getPosts(
        blogSlug: String,
        categoryIds: List<UUID>?,
        statuses: Set<PostStatus>,
        pageable: Pageable
    ): Page<PostSummaryInfo> {
        val postSummaries = postRepository.findPostSummaries(blogSlug, categoryIds, statuses, pageable)
        return postSummaries.map { PostSummaryInfo.from(it) }
    }

    @Transactional(readOnly = true)
    fun getPublishedPost(blogSlug: String, postSlug: String): PostContentInfo {
        val post = postRepository.findPublishedPost(blogSlug, postSlug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        return PostContentInfo.from(post)
    }

    @Transactional(readOnly = true)
    fun getPublishedPost(postId: UUID): PostContentInfo {
        val post = postRepository.findPublishedPost(postId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        return PostContentInfo.from(post)
    }

    @Transactional(readOnly = true)
    fun existsByCategoryIds(categoryIds: List<UUID>): Boolean {
        return postRepository.existsByCategoryIdIn(categoryIds)
    }

    @Transactional(readOnly = true)
    fun countPublishedPosts(blogId: UUID): Long {
        return postRepository.countByBlogIdAndStatus(blogId, PostStatus.PUBLISHED)
    }

    @Transactional
    fun createSamplePosts(userId: UUID, blogId: UUID, categoryIds: Map<String, UUID>, contents: Map<String, String>) {
        val posts = listOf(
            Post.create(
                userId = userId,
                blogId = blogId,
                categoryId = categoryIds["backend"],
                title = "코틀린(Kotlin)으로 시작하는 나의 첫 백엔드 개발",
                slug = "my-first-backend-with-kotlin",
                content = contents["backend"]!!,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now().minusDays(1)
            ),
            Post.create(
                userId = userId,
                blogId = blogId,
                categoryId = categoryIds["daily"],
                title = "새로운 시작, 나의 공간에 오신 것을 환영합니다",
                slug = "welcome-to-my-new-space",
                content = contents["daily"]!!,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now()
            )
        )
        postRepository.saveAll(posts)
    }

    fun validatePostOwner(userId: UUID, postId: UUID) {
        val post = postRepository.findById(postId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        if (!post.isAuthor(userId)) {
            throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
        }
    }

    fun deletePost(postId: UUID) {
        postRepository.deleteById(postId)
    }
}