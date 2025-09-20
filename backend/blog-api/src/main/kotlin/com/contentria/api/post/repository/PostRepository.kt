package com.contentria.api.post.repository

import com.contentria.api.blog.domain.Blog
import com.contentria.api.post.Post
import com.contentria.api.post.PostStatus
import com.contentria.api.post.dto.CategoryPostCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostRepository : JpaRepository<Post, UUID> {

    fun findAllByBlogAndStatus(blog: Blog, status: PostStatus, pageable: Pageable): Page<Post>

    // 카테고리별 게시물 수를 한번의 쿼리로 효율적으로 계산
    @Query("""
        SELECT new com.contentria.api.post.dto.CategoryPostCountDto(p.category.id, COUNT(p))
        FROM Post p
        WHERE p.blog = :blog AND p.status = 'PUBLISHED' AND p.category.id IS NOT NULL
        GROUP BY p.category.id
    """)
    fun countPostsByCategoryId(@Param("blog") blog: Blog): List<CategoryPostCountDto>
}