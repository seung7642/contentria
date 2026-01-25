package com.contentria.api.category.application

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.category.domain.Category
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {  }

@Service
class CategoryService(
    private val categoryInternalService: CategoryInternalService,
    private val categoryRepository: CategoryRepository,
) {
    @Transactional(readOnly = true)
    fun getCategoryInfo(categoryId: UUID): CategoryInfo {
        val category = categoryRepository.findById(categoryId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_CATEGORY)

        return CategoryInfo(
            id = category.id!!,
            name = category.name,
            slug = category.slug,
            parentId = category.parent?.id,
            level = -1,
            postCount = 0
        )
    }

    @Transactional(readOnly = true)
    fun getFlattenedCategoryInfos(blogId: UUID): List<CategoryInfo> {
        return categoryInternalService.getFlattenedCategoryInfos(blogId)
    }

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
}