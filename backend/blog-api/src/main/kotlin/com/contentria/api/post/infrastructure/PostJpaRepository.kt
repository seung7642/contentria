package com.contentria.api.post.infrastructure

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.domain.query.CategoryPostCount
import com.contentria.api.post.domain.query.PostSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostJpaRepository : JpaRepository<Post, UUID> {

    // 카테고리별 게시물 수를 한번의 쿼리로 효율적으로 계산
    @Query(
        """
        SELECT new com.contentria.api.post.domain.query.CategoryPostCount(p.categoryId, COUNT(p))
        FROM Post p
        WHERE p.blogId = :blogId 
            AND p.status = com.contentria.api.post.domain.PostStatus.PUBLISHED 
            AND p.categoryId IS NOT NULL
        GROUP BY p.categoryId
    """
    )
    fun findPostCountsByBlogId(@Param("blogId") blogId: UUID): List<CategoryPostCount>

    @Query("""
        SELECT new com.contentria.api.post.domain.query.PostSummary(
            p.id,
            p.slug,
            p.title,
            CASE 
                WHEN p.metaDescription IS NOT NULL AND TRIM(p.metaDescription) <> '' THEN p.metaDescription
                ELSE CONCAT(SUBSTRING(CAST(p.contentMarkdown AS string), 1, 100), '...')
            END,
            p.metaTitle,
            p.metaDescription,
            p.status,
            p.featuredImageUrl,
            p.publishedAt,
            p.createdAt,
            p.updatedAt,
            p.likeCount,
            p.viewCount,
            c.id,
            c.name
        )
        FROM Post p
        JOIN Blog b ON p.blogId = b.id
        LEFT JOIN Category c ON p.categoryId = c.id
        WHERE b.slug = :blogSlug 
            AND p.status IN :statuses 
            AND (:categoryIds IS NULL OR p.categoryId IN :categoryIds)
    """
    )
    fun findPostSummaries(
        blogSlug: String,
        categoryIds: List<UUID>?,
        statuses: Set<PostStatus>,
        pageable: Pageable
    ): Page<PostSummary>

    @Query("""
        SELECT p
        FROM Post p
        JOIN Blog b ON p.blogId = b.id 
        LEFT JOIN Category c ON p.categoryId = c.id
        JOIN User u ON b.userId = u.id
        WHERE b.slug = :blogSlug
            AND p.slug = :postSlug 
            AND p.status = com.contentria.api.post.domain.PostStatus.PUBLISHED
    """)
    fun findPublishedPost(blogSlug: String, postSlug: String): Post?

    fun findByIdAndStatus(postId: UUID, status: PostStatus): Post?

    @Query("""
        SELECT p.slug 
        FROM Post p
        WHERE p.blogId = :blogId
            AND (p.slug = :targetSlug OR p.slug LIKE CONCAT(:targetSlug, '-%'))
    """)
    fun findSlugsByPrefix(blogId: UUID, targetSlug: String): List<String>

    fun existsByCategoryIdIn(categoryIds: List<UUID>): Boolean

    fun countByBlogIdAndStatus(blogId: UUID, published: PostStatus): Long
}