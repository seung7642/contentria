package com.contentria.api.category.infrastructure

import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.query.CategoryWithCountView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CategoryJpaRepository : JpaRepository<Category, UUID> {

    fun findAllByBlogIdOrderByCreatedAtAsc(blogId: UUID): List<Category>

    @Query("""
        SELECT c.slug
        FROM Category c
        WHERE c.blogId = :blogId
            AND (c.slug = :targetSlug OR c.slug LIKE CONCAT(:targetSlug, '-%'))
    """)
    fun findSimilarSlugs(@Param("blogId") blogId: UUID, @Param("targetSlug") targetSlug: String): List<String>

    @Query("SELECT DISTINCT c FROM Category c JOIN Post p ON p.category = c WHERE c.id IN :categoryIds")
    fun findCategoriesWithPosts(@Param("categoryIds") categoryIds: List<UUID>): List<Category>

    @Query("""
        SELECT new com.contentria.api.category.domain.query.CategoryWithCountView(
            c.id, c.name, c.slug, c.parent.id, COUNT(p.id)
        )
        FROM Category c
        LEFT JOIN Post p ON c.id = p.categoryId
            AND p.blogId = :blogId
            AND p.status = com.contentria.api.post.domain.PostStatus.PUBLISHED
        WHERE c.blogId = :blogId
        GROUP by c.id, c.name, c.slug, c.parent.id
    """)
    fun findAllWithPostCount(@Param("blogId") blogId: UUID): List<CategoryWithCountView>
}