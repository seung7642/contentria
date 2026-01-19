package com.contentria.api.category.domain

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.stereotype.Component

/**
 * 도메인 서비스
 */
@Component
class CategoryValidator {
    fun validateInternalRules(toDelete: List<Category>, requestIds: Set<String>) {
        val hasOrphanedChildren = toDelete.asSequence()
            .filter { it.parent == null }
            .any { category ->
                category.children.any { child ->
                    child.id.toString() !in requestIds
                }
            }

        if (hasOrphanedChildren) {
            throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
        }
    }
}