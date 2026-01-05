package com.contentria.api.category.application

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.domain.Category
import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.category.application.dto.SyncCategoryCommand
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.category.domain.CategoryValidator
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.utils.SlugUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.get

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository,
    private val blogRepository: BlogRepository,
    private val categoryValidator: CategoryValidator
) {
    @Transactional(readOnly = true)
    fun getFlattenedCategories(blog: Blog): List<CategoryInfo> {
        val categories = categoryRepository.findAll(blog)

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
    fun syncCategories(blogId: UUID, commands: List<SyncCategoryCommand>) {
        val actorUserId = commands.firstOrNull()?.actorUserId
            ?: throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)

        val blog = blogRepository.findById(blogId)
            .orElseThrow { ContentriaException(ErrorCode.NOT_FOUND_BLOG) }

        if (blog.user.id != actorUserId) {
            throw ContentriaException(ErrorCode.FORBIDDEN_ACCESS_BLOG)
        }

        val existingCategories = categoryRepository.findAll(blog)
        val existingMap = existingCategories.associateBy { it.id.toString() }

        // 1. 삭제 처리
        val requestIds = commands.map { it.id }.toSet()
        val toDelete = existingCategories.filterNot { it.id.toString() in requestIds }
        if (toDelete.isNotEmpty()) {
            categoryValidator.validateDeletable(toDelete, requestIds)
            val sortedToDelete = toDelete.sortedByDescending { it.parent != null }
            categoryRepository.deleteAll(sortedToDelete)
        }

        // 2. Upsert 처리
//        val level0Requests = commands.filter { it.parentId == null }
//        val savedParentsMap = level0Requests.associate { request ->
//            val savedEntity = upsertOne(request, blog, null, existingMap[request.id])
//            request.id to savedEntity
//        }
//
//        val level1Requests = commands.filter { it.parentId != null }
//        level1Requests.forEach { request ->
//            val parentId = request.parentId
//            val parent = savedParentsMap[parentId]
//                ?: existingMap[parentId]
//                ?: throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
//
//            upsertOne(request, blog, parent, existingMap[request.id])
//        }

        val sortedCommand = commands.sortedWith(compareBy { it.parentId != null })
        val processedEntities = existingMap.toMutableMap()
        sortedCommand.forEach { command ->
            val parent = command.parentId?.let { parentId ->
                processedEntities[parentId]
                    ?: throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
            }

            val savedEntity = upsertOne(command, blog, parent, existingMap[command.id])
            processedEntities[command.id] = savedEntity
        }
    }

    /**
     * 반복문에서 단 건 API 호출을 하지만, 내부적으로 JDBC Batch 처리됨
     *   - https://docs.hibernate.org/orm/7.2/userguide/html_single/#batch-jdbcbatch
     */
    private fun upsertOne(request: SyncCategoryCommand, blog: Blog, parent: Category?, existing: Category?): Category {
        val finalSlug = if (existing != null && existing.name == request.name) {
            existing.slug
        } else {
            generateUniqueSlug(blog, request.name)
        }

        return if (existing == null) {
            val newCategory = Category.create(
                name = request.name,
                slug = finalSlug,
                order = request.order,
                parent = parent,
                blog = blog
            )
            categoryRepository.save(newCategory)
        } else {
            existing.update(
                name = request.name,
                slug = finalSlug,
                order = request.order,
                parent = parent
            )
            existing
        }
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