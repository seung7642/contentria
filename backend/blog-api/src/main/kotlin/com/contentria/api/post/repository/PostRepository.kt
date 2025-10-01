package com.contentria.api.post.repository

import com.contentria.api.blog.domain.Blog
import com.contentria.api.post.domain.Post
import com.contentria.api.post.dto.CategoryPostCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID> {

    // 카테고리별 게시물 수를 한번의 쿼리로 효율적으로 계산
    @Query("""
        SELECT new com.contentria.api.post.dto.CategoryPostCountDto(p.category.id, COUNT(p))
        FROM Post p
        WHERE p.blog = :blog AND p.status = 'PUBLISHED' AND p.category.id IS NOT NULL
        GROUP BY p.category.id
    """)
    fun countPostsByCategoryId(@Param("blog") blog: Blog): List<CategoryPostCountDto>

    @Query("""
        SELECT new com.contentria.api.post.repository.PostSummaryProjection(
            p.id,
            p.slug,
            p.title,
            CASE 
                WHEN p.metaDescription IS NOT NULL AND TRIM(p.metaDescription) <> '' THEN p.metaDescription
                ELSE CONCAT(SUBSTRING(CAST(p.contentMarkdown AS string), 1, 100), '...')
            END,
            p.metaTitle,
            p.metaDescription,
            p.featuredImageUrl,
            p.publishedAt,
            c.name,
            p.likeCount,
            p.viewCount
        )
        FROM Post p
        JOIN p.blog b
        LEFT JOIN p.category c
        WHERE b.slug = :blogSlug AND p.status = 'PUBLISHED'
        ORDER BY p.publishedAt DESC
    """)
    fun findPostSummariesByBlogSlug(blogSlug: String, pageable: Pageable): Page<PostSummaryProjection>

    @Query("""
        SELECT p
        FROM Post p
        JOIN FETCH p.blog b
        LEFT JOIN FETCH p.category c
        JOIN FETCH b.user u
        WHERE b.slug = :blogSlug
            AND p.slug = :postSlug 
            AND p.status = com.contentria.api.post.domain.PostStatus.PUBLISHED
    """)
    fun findPublishedByBlogsWithDetails(blogSlug: String, postSlug: String): Post?

}