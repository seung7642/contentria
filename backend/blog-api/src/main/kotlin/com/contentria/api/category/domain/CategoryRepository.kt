package com.contentria.api.category.domain

import com.contentria.api.blog.domain.Blog
import java.util.*

interface CategoryRepository {

    fun save(category: Category): Category
    fun delete(category: Category)
    fun deleteAll(categories: List<Category>)
    fun findById(id: UUID): Category?

    fun findAll(blog: Blog): List<Category>

    fun findByIdAndBlog(id: UUID, blog: Blog): Category?

    fun findSimilarSlugs(blog: Blog, targetSlug: String): List<String>

    fun findCategoriesWithPosts(categoryIds: List<UUID>): List<Category>
}