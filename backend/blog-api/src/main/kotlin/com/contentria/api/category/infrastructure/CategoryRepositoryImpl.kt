package com.contentria.api.category.infrastructure

import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.category.domain.query.CategoryWithCountView
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository
) : CategoryRepository {

    override fun save(category: Category): Category {
        return jpaRepository.save(category)
    }

    override fun delete(category: Category) {
        jpaRepository.delete(category)
    }

    override fun deleteAll(categories: List<Category>) {
        jpaRepository.deleteAll(categories)
    }

    override fun findById(id: UUID): Category? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun findAllByBlogId(blogId: UUID): List<Category> {
        return jpaRepository.findAllByBlogIdOrderByCreatedAtAsc(blogId)
    }

    override fun findCategoriesWithPosts(categoryIds: List<UUID>): List<Category> {
        return jpaRepository.findCategoriesWithPosts(categoryIds)
    }

    override fun findSimilarSlugs(
        blogId: UUID,
        targetSlug: String
    ): List<String> {
        return jpaRepository.findSimilarSlugs(blogId, targetSlug)
    }

    override fun findAllWithPostCount(blogId: UUID): List<CategoryWithCountView> {
        return jpaRepository.findAllWithPostCount(blogId)
    }
}