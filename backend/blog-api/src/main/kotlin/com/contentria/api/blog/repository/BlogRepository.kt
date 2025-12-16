package com.contentria.api.blog.repository

import com.contentria.api.blog.domain.Blog
import com.contentria.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BlogRepository : JpaRepository<Blog, UUID> {

    fun existsBySlug(slug: String): Boolean

    @Query("SELECT b.slug FROM Blog b WHERE b.user.id = :userId")
    fun findSlugsByUserId(@Param("userId") userId: UUID): List<String>

    fun findAllByUser(user: User): List<Blog>

    fun findBySlug(slug: String): Blog?
}