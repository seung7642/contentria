package com.contentria.api.category.infrastructure

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.CategoryRepository
import org.springframework.stereotype.Repository
import java.util.UUID

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

    override fun findAll(blog: Blog): List<Category> {
        return jpaRepository.findAllByBlogOrderByCreatedAtAsc(blog)
    }

    override fun findByIdAndBlog(
        id: UUID,
        blog: Blog
    ): Category? {
        return jpaRepository.findByIdAndBlog(id, blog)
    }

    override fun findSimilarSlugs(
        blog: Blog,
        targetSlug: String
    ): List<String> {
        return jpaRepository.findSimilarSlugs(blog, targetSlug)
    }

    override fun findCategoriesWithPosts(categoryIds: List<UUID>): List<Category> {
        return jpaRepository.findCategoriesWithPosts(categoryIds)
    }
}