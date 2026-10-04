package com.contentria.api.blog.infrastructure

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.domain.BlogRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BlogRepositoryImpl(
    private val jpaRepository: BlogJpaRepository
) : BlogRepository {

    override fun save(blog: Blog): Blog {
        return jpaRepository.save(blog)
    }

    override fun delete(blog: Blog) {
        jpaRepository.delete(blog)
    }

    override fun delete(blogs: List<Blog>) {
        jpaRepository.deleteAll(blogs)
    }

    override fun findById(id: UUID): Blog? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun existsBySlug(slug: String): Boolean {
        return jpaRepository.existsBySlug(slug)
    }

    override fun findAllByUserId(userId: UUID): List<Blog> {
        return jpaRepository.findAllByUserId(userId)
    }

    override fun findBySlug(slug: String): Blog? {
        return jpaRepository.findBySlug(slug)
    }

    override fun findByIdAndUserId(
        blogId: UUID,
        userId: UUID
    ): Blog? {
        return jpaRepository.findByIdAndUserId(blogId, userId)
    }
}