package com.contentria.api.category.domain

import com.contentria.api.category.domain.query.CategoryWithCountView
import java.util.*

interface CategoryRepository {

    fun save(category: Category): Category
    fun saveAndFlush(category: Category): Category
    fun delete(category: Category)
    fun deleteAll(categories: List<Category>)
    fun findById(id: UUID): Category?

    fun findAllByBlogId(blogId: UUID): List<Category>

    fun findSimilarSlugs(blogId: UUID, targetSlug: String): List<String>

    fun findAllWithPostCount(blogId: UUID): List<CategoryWithCountView>

    fun findAllCategoryIdsWithChildrenBySlug(blogSlug: String, categorySlug: String): List<UUID>
}