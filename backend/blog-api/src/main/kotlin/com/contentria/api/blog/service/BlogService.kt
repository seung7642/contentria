package com.contentria.api.blog.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.dto.BlogResponse
import com.contentria.api.blog.dto.CreateBlogRequest
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository,
    private val userService: UserService
) {

    @Transactional
    fun createBlog(userId: String, request: CreateBlogRequest): BlogResponse {
        val user = userService.findActiveUserById(userId)

        if (blogRepository.existsBySlug(request.slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val newBlog = blogRepository.save(
            Blog(
                slug = request.slug,
                title = user.email,
                user = user
            )
        )

        val savedBlog = blogRepository.save(newBlog)

        log.info { "Creating a new blog. email:${user.email}, slug:${savedBlog.slug}" }
        return BlogResponse.from(savedBlog)
    }

    fun findSlugsByUserId(userId: String): List<String> {
        return blogRepository.findSlugsByUserId(userId)
    }
}