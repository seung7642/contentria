package com.contentria.api.post.application

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.application.dto.PostDetailInfo
import com.contentria.api.post.application.dto.PostSummaryInfo
import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostRepository
import com.contentria.api.post.domain.PostSlugGenerator
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
    private val postRepository: PostRepository,
    private val postSlugGenerator: PostSlugGenerator
) {
    @Transactional
    fun createSamplePosts(blogId: UUID, categoryIds: Map<String, UUID>, contents: Map<String, String>) {
        val posts = listOf(
            Post.create(
                blogId = blogId,
                categoryId = categoryIds["backend"],
                title = "코틀린(Kotlin)으로 시작하는 나의 첫 백엔드 개발",
                slug = "my-first-backend-with-kotlin",
                content = contents["backendPost"]!!,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now().minusDays(1)
            ),
            Post.create(
                blogId = blogId,
                categoryId = categoryIds["daily"],
                title = "새로운 시작, 나의 공간에 오신 것을 환영합니다",
                slug = "welcome-to-my-new-space",
                content = contents["dailyPost"]!!,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now()
            )
        )
        postRepository.saveAll(posts)
    }

    @Transactional(readOnly = true)
    fun getPostsByBlogSlug(blogSlug: String, pageable: Pageable): Page<PostSummaryInfo> {
        val postSummaries = postRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
        return postSummaries.map { PostSummaryInfo.from(it) }
    }

    @Transactional(readOnly = true)
    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailInfo {
        val post = postRepository.findPublishedPostDetailView(blogSlug, postSlug)
            ?: throw ContentriaException(
                ErrorCode.NOT_FOUND_POST
            )

        return PostDetailInfo.from(post)
    }

    @Transactional(readOnly = true)
    fun existsByCategoryId(categoryId: UUID): Boolean {
        return postRepository.existsByCategoryId(categoryId)
    }

    @Transactional
    fun createPost(
        blogId: UUID,
        categoryId: UUID,
        title: String,
        content: String,
        summary: String,
        status: PostStatus
    ) : Post {
        val uniqueSlug = postSlugGenerator.generate(blogId, title)

        val newPost = Post.create(
            blogId = blogId,
            categoryId = categoryId,
            slug = uniqueSlug,
            title = title,
            content = content,
            summary = summary,
            metaTitle = title,
            metaDescription = summary,
            status = status,
            publishedAt = if (status.isPublished()) ZonedDateTime.now() else null
        )

        return postRepository.save(newPost)
    }
}