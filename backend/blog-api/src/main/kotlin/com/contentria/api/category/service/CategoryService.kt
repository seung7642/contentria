package com.contentria.api.category.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.dto.CategoryNodeInfo
import com.contentria.api.category.domain.Category
import com.contentria.api.category.dto.CategoryResponse
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
    fun getCategoryTreeWithPostCounts(blog: Blog): List<CategoryNodeInfo> {
        val allCategories = categoryRepository.findAllByBlog(blog)
        val postCounts: Map<UUID, Long> = postRepository.countPostsByCategoryId(blog)
            .associate { it.categoryId to it.postCount }

        val totalPostCount = postCounts.values.sum()
        val tree = buildCategoryTree(allCategories, postCounts)

        val allViewCategory = CategoryNodeInfo(null, "전체보기", "total", totalPostCount, emptyList())
        return listOf(allViewCategory) + tree
    }

    private fun buildCategoryTree(categories: List<Category>, postCounts: Map<UUID, Long>): List<CategoryNodeInfo> {
        val nodeMap = categories.associate { it.id to CategoryNodeInfo.from(it, postCounts[it.id] ?: 0) }
        val rootNodes = mutableListOf<CategoryNodeInfo>()
        categories.forEach { category ->
            val node = nodeMap.getValue(category.id)
            if (category.parent == null) {
                rootNodes.add(node)
            } else {
                val parentNode = nodeMap[category.parent!!.id]
                (parentNode?.children as? MutableList)?.add(node)
            }
        }

        rootNodes.forEach { updateTotalPostCount(it) }
        return rootNodes
    }

    private fun updateTotalPostCount(node: CategoryNodeInfo): Long {
        if (node.children.isEmpty()) {
            return node.postCount
        }
        val childrenTotal = node.children.sumOf { updateTotalPostCount(it) }
        node.postCount += childrenTotal
        return node.postCount
    }

    fun getCategoriesForDropdown(blog: Blog): List<CategoryResponse> {
        val categories = categoryRepository.findAllByBlogOrderByCreatedAtAsc(blog)

        val groupedByParent = categories.groupBy { it.parent?.id }

        val result = mutableListOf<CategoryResponse>()
        flattenCategories(groupedByParent, null, 0, result)

        return result
    }

    private fun flattenCategories(
        groupedCategories: Map<UUID?, List<Category>>,
        parentId: UUID?,
        level: Int,
        result: MutableList<CategoryResponse>
    ) {
        val children = groupedCategories[parentId] ?: return

        for (category in children) {
            result.add(
                CategoryResponse(
                    id = category.id!!,
                    name = category.name,
                    slug = category.slug,
                    parentId = parentId,
                    level = level
                )
            )
            flattenCategories(groupedCategories, category.id, level + 1, result)
        }
    }
}