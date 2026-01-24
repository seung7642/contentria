package com.contentria.api.blog.application

import com.contentria.api.blog.application.dto.BlogInfo
import com.contentria.api.blog.application.dto.BlogLayoutInfo
import com.contentria.api.blog.application.dto.CreateBlogCommand
import com.contentria.api.category.application.CategoryService
import com.contentria.api.post.application.PostService
import com.contentria.api.user.application.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.util.*

private val log = KotlinLogging.logger {  }

@Component
class BlogFacade(
    private val blogService: BlogService,
    private val userService: UserService,
    private val categoryService: CategoryService,
    private val postService: PostService,
    private val resourceLoader: ResourceLoader
) {
    @Transactional(readOnly = true)
    fun getMyBlog(userId: UUID): List<BlogInfo> = blogService.getBlogInfos(userId)

    @Transactional(readOnly = true)
    fun getBlogLayout(slug: String): BlogLayoutInfo {
        val blog = blogService.getBlogInfo(slug)

        val ownerInfo = userService.getActiveUserInfo(blog.userId)

        val categories = categoryService.getFlattenedCategoryInfos(blog.blogId)

        return BlogLayoutInfo(
            blog = blog,
            owner = ownerInfo,
            categories = categories
        )
    }

    @Transactional
    fun createBlogWithSamples(userId: UUID, request: CreateBlogCommand): BlogInfo {
        val user = userService.getActiveUserInfo(userId)

        val savedBlogInfo = blogService.createBlog(userId, request.slug, user.email)

        val createdCategoryIds = categoryService.createSampleCategories(savedBlogInfo.blogId)
        val sampleContents = mapOf(
            "backendPost" to readMarkdownContent("classpath:samples/backend-post.md"),
            "dailyPost" to readMarkdownContent("classpath:samples/daily-post.md")
        )

        postService.createSamplePosts(savedBlogInfo.blogId, createdCategoryIds, sampleContents)

        log.info { "Created blog with samples. email:${user.email}, slug:${savedBlogInfo.slug}" }

        return savedBlogInfo
    }

    private fun readMarkdownContent(resourcePath: String): String {
        return try {
            val resource = resourceLoader.getResource(resourcePath)
            resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        } catch (e: Exception) {
            log.warn(e) { "Failed to load sample markdown: $resourcePath" }
            "Sample content"
        }
    }
}