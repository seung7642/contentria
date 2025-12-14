package com.contentria.api.category.repository

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findAllByBlog(blog: Blog): List<Category>

    fun findAllByBlogOrderByCreatedAtAsc(blog: Blog): List<Category>
}