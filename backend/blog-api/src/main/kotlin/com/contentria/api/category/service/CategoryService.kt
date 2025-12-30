package com.contentria.api.category.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.domain.Category
import com.contentria.api.category.dto.CategoryInfo
import com.contentria.api.category.repository.CategoryRepository
import com.contentria.api.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository
) {
    @Transactional(readOnly = true)
    fun getFlattenedCategories(blog: Blog): List<CategoryInfo> {
        val categories = categoryRepository.findAllByBlogOrderByCreatedAtAsc(blog)

        val postCounts = postRepository.findPostCountsByBlog(blog)
            .associate { it.categoryId to it.postCount }

        val groupedByParent = categories.groupBy { it.parent?.id }

        val result = mutableListOf<CategoryInfo>()
        flattenRecursively(groupedByParent, postCounts, null, 0, result)

        return result
    }

    private fun flattenRecursively(
        groupedCategories: Map<UUID?, List<Category>>,
        postCounts: Map<UUID, Long>,
        parentId: UUID?,
        level: Int,
        result: MutableList<CategoryInfo>
    ) {
        val children = groupedCategories[parentId] ?: return

        for (category in children) {
            val count = postCounts[category.id] ?: 0L
            result.add(
                CategoryInfo(
                    id = category.id!!,
                    name = category.name,
                    slug = category.slug,
                    parentId = parentId,
                    level = level,
                    postCount = count
                )
            )
            flattenRecursively(groupedCategories, postCounts, category.id, level + 1, result)
        }
    }
}