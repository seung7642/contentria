package com.contentria.api.category.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.domain.Category
import com.contentria.api.category.dto.CategoryInfo
import com.contentria.api.category.dto.CreateCategoryCommand
import com.contentria.api.category.dto.CreateCategoryInfo
import com.contentria.api.category.repository.CategoryRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.utils.SlugUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository,
    private val blogRepository: BlogRepository
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

    @Transactional
    fun createCategory(blogId: UUID, command: CreateCategoryCommand): CreateCategoryInfo {
        val blog = blogRepository.findById(blogId)
            .orElseThrow { ContentriaException(ErrorCode.NOT_FOUND_BLOG) }

        var parentCategory: Category? = null
        if (command.parentId != null) {
            parentCategory = categoryRepository.findById(command.parentId)
                .orElseThrow { ContentriaException(ErrorCode.NOT_FOUND_CATEGORY) }

            if (parentCategory.blog.id != blog.id) {
                throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
            }

            if (parentCategory.parent != null) {
                throw ContentriaException(ErrorCode.MAX_CATEGORY_LEVEL_EXCEEDED)
            }
        }

        if (categoryRepository.existsByBlogAndParentAndName(blog, parentCategory, command.name)) {
            throw ContentriaException(ErrorCode.DUPLICATE_CATEGORY_NAME)
        }

        val slug = generateUniqueSlug(blog, command.name)

        val category = Category(
            name = command.name,
            slug = slug,
            parent = parentCategory,
            blog = blog
        )

        parentCategory?.children?.add(category)

        val savedCategory = categoryRepository.save(category)
        return CreateCategoryInfo.from(savedCategory)
    }

    private fun generateUniqueSlug(blog: Blog, name: String): String {
        var baseSlug = SlugUtils.toSlug(name)

        val existingSlugs = categoryRepository.findSimilarSlugs(blog, baseSlug)
        if (existingSlugs.isEmpty()) {
            return baseSlug
        }

        val maxSuffix = existingSlugs.asSequence()
            .mapNotNull { slug ->
                if (slug === baseSlug) {
                    0
                } else {
                    val suffixStr = slug.substringAfter("$baseSlug-")
                    suffixStr.toIntOrNull()
                }
            }
            .maxOrNull() ?: 0

        return "$baseSlug-${maxSuffix + 1}"
    }
}