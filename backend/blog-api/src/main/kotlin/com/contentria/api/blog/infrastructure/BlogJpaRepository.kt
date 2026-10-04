package com.contentria.api.blog.infrastructure

import com.contentria.api.blog.domain.Blog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BlogJpaRepository : JpaRepository<Blog, UUID> {

    fun existsBySlug(slug: String): Boolean

    fun findAllByUserId(userId: UUID): List<Blog>

    fun findBySlug(slug: String): Blog?

    fun findByIdAndUserId(blogId: UUID, userId: UUID): Blog?
}