package com.contentria.api.category.application

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.category.application.dto.SyncCategoryCommand
import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.category.domain.CategorySlugGenerator
import com.contentria.api.category.domain.CategoryValidator
import com.contentria.api.category.domain.query.CategoryWithCountView
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.get

private val log = KotlinLogging.logger {  }

@Service
class CategoryInternalService(
    private val categoryRepository: CategoryRepository,
    private val categoryValidator: CategoryValidator,
    private val categorySlugGenerator: CategorySlugGenerator
) {
    @Transactional(readOnly = true)
    fun getCategories(blogId: UUID): List<Category> {
        return categoryRepository.findAllByBlogId(blogId)
    }

    @Transactional(readOnly = true)
    fun getFlattenedCategoryInfos(blogId: UUID): List<CategoryInfo> {
        val categoriesWithCount = categoryRepository.findAllWithPostCount(blogId)

        val groupedByParent = categoriesWithCount.groupBy { it.parentId }
        val categoryMap = categoriesWithCount.associateBy { it.id }

        val totalCountMap = mutableMapOf<UUID, Long>()

        fun calculateTotalCount(categoryId: UUID): Long {
            if (totalCountMap.containsKey(categoryId)) {
                return totalCountMap[categoryId]!!
            }

            val currentCategory = categoryMap[categoryId] ?: return 0
            val currentCount = currentCategory.postCount

            val children = groupedByParent[categoryId] ?: emptyList()
            val childrenTotalCount = children.sumOf { calculateTotalCount(it.id) }

            val total = currentCount + childrenTotalCount
            totalCountMap[categoryId] = total

            return total
        }

        categoriesWithCount.forEach { calculateTotalCount(it.id) }

        val result = mutableListOf<CategoryInfo>()
        flattenRecursively(groupedByParent, totalCountMap, null, 0, result)

        return result
    }

    private fun flattenRecursively(
        groupedCategories: Map<UUID?, List<CategoryWithCountView>>,
        totalCountMap: Map<UUID, Long>,
        parentId: UUID?,
        level: Int,
        result: MutableList<CategoryInfo>
    ) {
        val children = groupedCategories[parentId] ?: return

        for (category in children) {
            val totalCount = totalCountMap[category.id] ?: category.postCount

            result.add(
                CategoryInfo(
                    id = category.id,
                    name = category.name,
                    slug = category.slug,
                    parentId = parentId,
                    level = level,
                    postCount = totalCount
                )
            )
            flattenRecursively(groupedCategories, totalCountMap, category.id, level + 1, result)
        }
    }

    @Transactional
    fun applySync(blogId: UUID, commands: List<SyncCategoryCommand>, existingCategories: List<Category>) {
        val requestIds = commands.map { it.id }.toSet()
        val existingMap = existingCategories.associateBy { it.id.toString() }

        val toDelete = existingCategories.filter { it.id.toString() !in requestIds }

        if (toDelete.isNotEmpty()) {
            val futureParentMap = commands.associate { it.id to it.parentId }
            categoryValidator.validateReferentialIntegrity(toDelete, futureParentMap)

            val sortedToDelete = toDelete.sortedByDescending { it.parent != null }
            categoryRepository.deleteAll(sortedToDelete)
        }

        val savedParentsMap = mutableMapOf<String, Category>()

        commands.filter { it.parentId == null }.forEach { command ->
            val saved = upsertCategory(blogId, command, null, existingMap[command.id])
            savedParentsMap[command.id] = saved
        }

        commands.filter { it.parentId != null }.forEach { command ->
            val parent = savedParentsMap[command.parentId]
                ?: existingMap[command.parentId]
                ?: throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)

            upsertCategory(blogId, command, parent, existingMap[command.id])
        }
    }

    /**
     * 반복문에서 단 건 API 호출을 하지만, 내부적으로 JDBC Batch 처리됨
     *   - https://docs.hibernate.org/orm/7.2/userguide/html_single/#batch-jdbcbatch
     */
    private fun upsertCategory(
        blogId: UUID,
        command: SyncCategoryCommand,
        parent: Category?,
        existing: Category?
    ): Category {
        val slug = if (existing == null || existing.shouldUpdateSlug(command.name)) {
            categorySlugGenerator.generate(blogId, command.name)
        } else {
            existing.slug
        }

        return if (existing == null) {
            val newCategory = Category.create(
                blogId = blogId,
                name = command.name,
                slug = slug,
                order = command.order,
                parent = parent,
            )
            categoryRepository.save(newCategory)
        } else {
            existing.update(
                name = command.name,
                slug = slug,
                order = command.order,
                parent = parent
            )
            existing
        }
    }
}