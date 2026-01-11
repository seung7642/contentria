package com.contentria.api.blog.domain

import com.contentria.api.user.domain.User
import java.util.UUID

interface BlogRepository {

    fun save(blog: Blog): Blog
    fun delete(blog: Blog)
    fun delete(blogs: List<Blog>)
    fun findById(id: UUID): Blog?

    fun existsBySlug(slug: String): Boolean

    fun findAllByUserId(userId: UUID): List<Blog>

    fun findBySlug(slug: String): Blog?

    fun findByIdAndUserId(blogId: UUID, userId: UUID): Blog?
}