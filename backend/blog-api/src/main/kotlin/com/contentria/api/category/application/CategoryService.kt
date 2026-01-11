package com.contentria.api.category.application

import com.contentria.api.blog.domain.BlogRepository
import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.category.application.dto.SyncCategoryCommand
import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.category.domain.CategorySlugGenerator
import com.contentria.api.category.domain.CategoryValidator
import com.contentria.api.category.domain.query.CategoryWithCountView
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val blogRepository: BlogRepository,
    private val categoryValidator: CategoryValidator,
    private val categorySlugGenerator: CategorySlugGenerator
) {
    @Transactional
    fun createSampleCategories(blogId: UUID): Map<String, UUID> {
        val tech = categoryRepository.save(Category.create(name = "기술", slug = "tech", blogId = blogId, order = 0))
        val backend = categoryRepository.save(Category.create(name = "백엔드", slug = "backend", blogId = blogId, order = 0, parent = tech))
        val daily = categoryRepository.save(Category.create(name = "일상", slug = "daily", blogId = blogId, order = 1))
        return mapOf(
            "backend" to backend.id!!,
            "daily" to daily.id!!
        )
    }

    @Transactional(readOnly = true)
    fun validateCategoryBelongsToBlog(categoryId: UUID, blogId: UUID) {
        val category = categoryRepository.findById(categoryId)
            ?: throw ContentriaException(
                ErrorCode.NOT_FOUND_CATEGORY
            )

        if (category.blogId != blogId) {
            throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
        }
    }

    @Transactional(readOnly = true)
    fun getFlattenedCategories(blogId: UUID): List<CategoryInfo> {
        val categoriesWithCount = categoryRepository.findAllWithPostCount(blogId)

        val groupedByParent = categoriesWithCount.groupBy { it.parentId }

        val result = mutableListOf<CategoryInfo>()
        flattenRecursively(groupedByParent, null, 0, result)

        return result
    }

    private fun flattenRecursively(
        groupedCategories: Map<UUID?, List<CategoryWithCountView>>,
        parentId: UUID?,
        level: Int,
        result: MutableList<CategoryInfo>
    ) {
        val children = groupedCategories[parentId] ?: return

        for (category in children) {
            result.add(
                CategoryInfo(
                    id = category.id!!,
                    name = category.name,
                    slug = category.slug,
                    parentId = parentId,
                    level = level,
                    postCount = category.postCount
                )
            )
            flattenRecursively(groupedCategories, category.id, level + 1, result)
        }
    }

    @Transactional
    fun syncCategories(blogId: UUID, commands: List<SyncCategoryCommand>) {
        val actorUserId = commands.firstOrNull()?.actorUserId
            ?: throw ContentriaException(
                ErrorCode.INVALID_INPUT_VALUE
            )

        val blog = blogRepository.findById(blogId)
            .orElseThrow {
                ContentriaException(
                    ErrorCode.NOT_FOUND_BLOG
                )
            }

        if (blog.user.id != actorUserId) {
            throw ContentriaException(ErrorCode.FORBIDDEN_ACCESS_BLOG)
        }

        // 데이터 준비
        val existingCategories = categoryRepository.findAllByBlogId(blogId)
        val existingMap = existingCategories.associateBy { it.id.toString() }
        val requestIds = commands.map { it.id }.toSet()

        // 삭제 처리
        val toDelete = existingCategories.filterNot { it.id.toString() in requestIds }
        if (toDelete.isNotEmpty()) {
            categoryValidator.validateDeletable(toDelete, requestIds)

            // 삭제 순서 (자식->부모)는 기술적인 문제에 가까우므로 여기서 처리하거나 리포에 위임
            val sortedToDelete = toDelete.sortedByDescending { it.parent != null }
            categoryRepository.deleteAll(sortedToDelete)
        }

        // 생성/수정 처리
        // 부모-자식 순서 처리는 '상태 의존적'이므로 응용 서비스가 조율하는 것이 자연스러움
        val savedParentsMap = mutableMapOf<String, Category>()

        commands.filter { it.parentId == null }.forEach { command ->
            val saved = upsertCategory(blogId, command, null, existingMap[command.id])
            savedParentsMap[command.id] = saved
        }

        commands.filter { it.parentId != null }.forEach { command ->
            val parent = savedParentsMap[command.parentId]
                ?: existingMap[command.parentId]
                ?: throw ContentriaException(
                    ErrorCode.INVALID_INPUT_VALUE
                )

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