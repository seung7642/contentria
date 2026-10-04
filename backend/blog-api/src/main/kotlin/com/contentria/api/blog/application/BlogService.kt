package com.contentria.api.blog.application

import com.contentria.api.blog.application.dto.BlogInfo
import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.domain.BlogRepository
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository
) {
    @Transactional(readOnly = true)
    fun getBlogInfo(slug: String): BlogInfo {
        val blog = blogRepository.findBySlug(slug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)

        return BlogInfo.from(blog)
    }

    @Transactional(readOnly = true)
    fun getBlogInfo(blogId: UUID): BlogInfo {
        val blog = blogRepository.findById(blogId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)

        return BlogInfo.from(blog)
    }

    @Transactional(readOnly = true)
    fun getBlogInfos(userId: UUID): List<BlogInfo> {
        val blogs = blogRepository.findAllByUserId(userId)
        return blogs.map { BlogInfo.from(it) }
    }

    @Transactional
    fun createBlog(userId: UUID, slug: String, title: String): BlogInfo {
        if (blogRepository.existsBySlug(slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val savedBlog = blogRepository.save(
            Blog(
                slug = slug,
                title = title,
                userId = userId
            )
        )
        return BlogInfo.from(savedBlog)
    }

    /** 블로그 제목/설명 수정 — 소유자만 가능. slug는 URL 정체성이라 변경 대상이 아니다. */
    @Transactional
    fun updateBlogSettings(userId: UUID, blogSlug: String, title: String, description: String?): BlogInfo {
        val blog = blogRepository.findBySlug(blogSlug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)

        if (!blog.isOwner(userId)) {
            throw ContentriaException(ErrorCode.FORBIDDEN_ACCESS_BLOG)
        }

        blog.updateSettings(title = title, description = description)
        return BlogInfo.from(blog)
    }

    @Transactional(readOnly = true)
    fun validateBlogOwner(blogId: UUID, userId: UUID) {
        val blog = blogRepository.findById(blogId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)

        if (!blog.isOwner(userId)) {
            throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
        }
    }
}