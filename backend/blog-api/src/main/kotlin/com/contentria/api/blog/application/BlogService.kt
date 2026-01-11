package com.contentria.api.blog.application

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.domain.BlogRepository
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository
) {
    @Transactional
    fun createBlog(userId: UUID, slug: String, title: String): Blog {
        if (blogRepository.existsBySlug(slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val blog = Blog(
            slug = slug,
            title = title,
            userId = userId
        )
        return blogRepository.save(blog)
    }

//    @Transactional(readOnly = true)
//    fun getBlogByBlogId(blogId: UUID): Blog {
//        return blogRepository.findById(blogId)
//            .orElseThrow { ContentriaException(ErrorCode.NOT_FOUND_BLOG) }
//    }

    @Transactional
    fun getBlogBySlug(slug: String): Blog {
        return blogRepository.findBySlug(slug)
            ?: throw ContentriaException(
                ErrorCode.NOT_FOUND_BLOG
            )
    }

    @Transactional(readOnly = true)
    fun validateBlogOwner(blogId: UUID, userId: UUID) {
        val blog = (blogRepository.findById(blogId)
            ?: throw ContentriaException(
                ErrorCode.NOT_FOUND_BLOG
            ))

        if (!blog.isOwner(userId)) {
            throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
        }
    }
}