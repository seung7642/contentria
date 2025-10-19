package com.contentria.api.post.service

import com.contentria.api.blog.dto.OwnerInfo
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.dto.CreateNewPostCommand
import com.contentria.api.post.dto.CreateNewPostInfo
import com.contentria.api.post.dto.PostDetailAndOwnerInfo
import com.contentria.api.post.dto.PostDetailInfo
import com.contentria.api.post.dto.PostSummaryInfo
import com.contentria.api.post.repository.PostRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class PostService(
    private val postRepository: PostRepository,
    private val markdownService: MarkdownService
) {

    @Transactional(readOnly = true)
    fun getPostsByBlogSlug(blogSlug: String, page: Int, size: Int): Page<PostSummaryInfo> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val postSummaries = postRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
        return postSummaries.map { PostSummaryInfo.from(it) }
    }

    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailAndOwnerInfo {
        val post = postRepository.findPublishedByBlogsWithDetails(blogSlug, postSlug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        return PostDetailAndOwnerInfo(
            post = PostDetailInfo.from(post),
            owner = OwnerInfo.from(post.blog.user)
        )
    }

    @Transactional
    fun createNewPost(command: CreateNewPostCommand): CreateNewPostInfo {
        // 1. 비즈니스 유효성 검증
        validateBusinessRules(command)

        // 2. contentMarkdown을 HTML로 변환 & HTML Sanitization
        val contentHtml = markdownService.convertToHtml(command.contentMarkdown)

        // 3. Post 엔티티 생성 및 저장

        return CreateNewPostInfo(
            postId = UUID.randomUUID(),
            slug = "",
            title = "",
            metaTitle = "",
            metaDescription = "",
            publishedAt = null,
            status = PostStatus.DRAFT,
            categoryName = null
        )
    }

    private fun validateBusinessRules(command: CreateNewPostCommand) {
        // 각 비즈니스 규칙에 위반될 시, 매핑되는 ErrorCode로 ContentriaException을 던집니다.

        // 1. 사용자 ID 존재 여부 검증

        // 2. 블로그 존재 여부

        // 3. 카테고리 ID 존재 여부 (카테고리 ID가 주어진 경우)
        command.categoryId?.let { categoryId ->
            // TODO: 카테고리 존재 여부 검증
        }
    }
}