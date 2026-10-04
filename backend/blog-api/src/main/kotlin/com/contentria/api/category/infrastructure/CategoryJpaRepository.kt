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

    @Query(value = """
        WITH RECURSIVE category_tree AS (
            SELECT c.id
            FROM categories c
            INNER JOIN blogs b ON c.blog_id = b.id
            WHERE b.slug = :blogSlug AND c.slug = :categorySlug
            
            UNION ALL
            
            SELECT c.id
            FROM categories c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
        )
        SELECT id FROM category_tree
    """, nativeQuery = true)
    fun findAllCategoryIdsWithChildrenBySlug(blogSlug: String, categorySlug: String): List<UUID>
}