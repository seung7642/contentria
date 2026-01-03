package com.contentria.api.category.repository

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findAllByBlog(blog: Blog): List<Category>

    fun findAllByBlogOrderByCreatedAtAsc(blog: Blog): List<Category>

    fun findByIdAndBlog(id: UUID, blog: Blog): Category?

    fun existsByBlogAndParentAndName(blog: Blog, parent: Category?, name: String): Boolean

    fun existsByBlogAndSlug(blog: Blog, slug: String): Boolean

    @Query("""
        SELECT c.slug
        FROM Category c
        WHERE c.blog = :blog
            AND (c.slug = :targetSlug OR c.slug LIKE CONCAT(:targetSlug, '-%'))
    """)
    fun findSimilarSlugs(@Param("blog") blog: Blog, @Param("targetSlug") targetSlug: String): List<String>
}