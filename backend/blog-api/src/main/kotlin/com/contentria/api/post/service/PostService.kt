package com.contentria.api.post.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.domain.Post
import com.contentria.api.post.dto.CreateNewPostCommand
import com.contentria.api.post.dto.CreateNewPostInfo
import com.contentria.api.post.dto.PostDetailInfo
import com.contentria.api.post.dto.PostSummaryInfo
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.utils.SlugUtils
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
    private val blogRepository: BlogRepository,
    private val categoryRepository: CategoryRepository,
    private val markdownService: MarkdownService
) {

    @Transactional(readOnly = true)
    fun getPostsByBlogSlug(blogSlug: String, pageable: Pageable): Page<PostSummaryInfo> {
//        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val postSummaries = postRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
        return postSummaries.map { it.copy(summary = markdownService.extractSummary(it.summary)) }
            .map { PostSummaryInfo.from(it) }
    }

    @Transactional(readOnly = true)
    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailInfo {
        val post = postRepository.findPublishedByBlogsWithDetails(blogSlug, postSlug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        return PostDetailInfo.from(post)
    }

    @Transactional
    fun createNewPost(userId: UUID, command: CreateNewPostCommand): CreateNewPostInfo {
        val blog = blogRepository.findByIdAndUserId(command.blogId, userId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)

        val category = categoryRepository.findByIdAndBlog(command.categoryId, blog)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_CATEGORY)

        val rawSlug = SlugUtils.toSlug(command.title)
        val uniqueSlug = resolveUniqueSlug(blog, rawSlug)

        val summary = markdownService.extractSummary(command.contentMarkdown)
        val savedPost = postRepository.save(
            Post(
                blog = blog,
                category = category,
                slug = uniqueSlug,
                title = command.title,
                contentMarkdown = command.contentMarkdown,
                summary = summary,
                metaTitle = command.title,
                metaDescription = summary,
                status = command.status,
                publishedAt = ZonedDateTime.now().takeIf { command.status.isPublished() }
            ))
        log.info { "Created new post: $savedPost" }

        return CreateNewPostInfo.from(savedPost)
    }

    private fun resolveUniqueSlug(blog: Blog, rawSlug: String): String {
        val existingSlugs = postRepository.findSlugsByPrefix(blog, rawSlug)

        if (existingSlugs.isEmpty()) {
            return rawSlug
        }

        if (!existingSlugs.contains(rawSlug)) {
            return rawSlug
        }

        val maxSuffix = existingSlugs.asSequence()
            .mapNotNull { slug ->
                val suffix = slug.removePrefix(rawSlug)
                if (suffix.matches(Regex("-\\d+"))) {
                    suffix.substring(1).toLong()
                } else {
                    null
                }
            }
            .maxOrNull() ?: 0L

        return "$rawSlug-${maxSuffix + 1}"
    }
}