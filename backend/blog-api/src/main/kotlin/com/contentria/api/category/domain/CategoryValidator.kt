package com.contentria.api.category.domain

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.stereotype.Component

/**
 * 도메인 서비스
 */
@Component
class CategoryValidator(
    private val categoryRepository: CategoryRepository
) {
    fun validateDeletable(toDelete: List<Category>, requestIds: Set<String>) {
        val toDeleteIds = toDelete.map { it.id!! }
        val categoriesWithPosts = categoryRepository.findCategoriesWithPosts(toDeleteIds)
        if (categoriesWithPosts.isNotEmpty()) {
            throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
        }

        val hasOrphanedChildren = toDelete
            .asSequence()
            .filter { it.parent == null }
            .any { it.children.any { it.id.toString() in requestIds } }

        if (hasOrphanedChildren) {
            throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
        }
    }
}