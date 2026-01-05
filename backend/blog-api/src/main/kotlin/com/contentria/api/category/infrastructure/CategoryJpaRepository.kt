package com.contentria.api.category.infrastructure

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface CategoryJpaRepository : JpaRepository<Category, UUID> {

    fun findAllByBlog(blog: Blog): List<Category>

    fun findAllByBlogOrderByCreatedAtAsc(blog: Blog): List<Category>

    fun findByIdAndBlog(id: UUID, blog: Blog): Category?

    @Query("""
        SELECT c.slug
        FROM Category c
        WHERE c.blog = :blog
            AND (c.slug = :targetSlug OR c.slug LIKE CONCAT(:targetSlug, '-%'))
    """)
    fun findSimilarSlugs(@Param("blog") blog: Blog, @Param("targetSlug") targetSlug: String): List<String>

    @Query("SELECT DISTINCT c FROM Category c JOIN Post p ON p.category = c WHERE c.id IN :categoryIds")
    fun findCategoriesWithPosts(@Param("categoryIds") categoryIds: List<UUID>): List<Category>
}