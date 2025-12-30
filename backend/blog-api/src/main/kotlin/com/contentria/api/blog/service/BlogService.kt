package com.contentria.api.blog.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.dto.*
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.domain.Category
import com.contentria.api.category.repository.CategoryRepository
import com.contentria.api.category.service.CategoryService
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.user.dto.UserSummaryInfo
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository,
    private val userService: UserService,
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository,
    private val resourceLoader: ResourceLoader
) {
    @Transactional
    fun createBlog(userId: UUID, request: CreateBlogCommand): CreateBlogInfo {
        val user = userService.findActiveUserById(userId)

        if (blogRepository.existsBySlug(request.slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val savedBlog = blogRepository.save(
            Blog(
                slug = request.slug,
                title = user.email,
                user = user
            )
        )
        addSampleContentFor(savedBlog)

        log.info { "Creating a new blog. email:${user.email}, slug:${savedBlog.slug}" }
        return CreateBlogInfo.from(savedBlog)
    }

    private fun addSampleContentFor(blog: Blog) {
        val techCategory = categoryRepository.save(Category(name = "기술", slug = "tech", blog = blog, parent = null))
        val backendCategory = categoryRepository.save(Category(name = "백엔드", slug = "backend", blog = blog, parent = techCategory))
        val dailyCategory = categoryRepository.save(Category(name = "일상", slug = "daily", blog = blog, parent = null))

        val dailyPostMarkdown = readMarkdownContent("classpath:samples/backend-post.md")
        val backendPostMarkdown = readMarkdownContent("classpath:samples/daily-post.md")

        val postsToCreate = listOf(
            Post(
                blog = blog,
                category = backendCategory,
                title = "코틀린(Kotlin)으로 시작하는 나의 첫 백엔드 개발",
                slug = "my-first-backend-with-kotlin",
                contentMarkdown = backendPostMarkdown,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now().minusDays(1)
            ),
            Post(
                blog = blog,
                category = dailyCategory,
                title = "새로운 시작, 나의 공간에 오신 것을 환영합니다",
                slug = "welcome-to-my-new-space",
                contentMarkdown = dailyPostMarkdown,
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now()
            )
        )

        postRepository.saveAll(postsToCreate)
    }

    private fun readMarkdownContent(resourcePath: String): String {
        val resource = resourceLoader.getResource(resourcePath)
        return resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    }

    @Transactional(readOnly = true)
    fun getBlogDetailBySlug(slug: String): BlogLayoutInfo {
        val blog = blogRepository.findBySlug(slug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)
        val user = blog.user

        return BlogLayoutInfo(
            blog = BlogInfo.from(blog),
            owner = UserSummaryInfo.from(user),
            categories = categoryService.getCategoryTreeWithPostCounts(blog)
        )
    }

    @Transactional(readOnly = true)
    fun getBlogByBlogId(blogId: UUID): Blog {
        return blogRepository.findById(blogId)
            .orElseThrow { ContentriaException(ErrorCode.NOT_FOUND_BLOG) }
    }
}