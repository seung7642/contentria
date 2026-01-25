package com.contentria.api.category.domain

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.stereotype.Component

@Component
class CategoryValidator {

    /**
     * Validates referential integrity for the category deletion process.
     *
     * This method ensures that no surviving category references a category scheduled for deletion
     * as its parent. It validates the **target state** (represented by [futureParentMap]) rather than
     * the current database state.
     *
     * If a category is being deleted, any category that intends to keep it as a parent
     * will cause this validation to fail.
     *
     * @param deletingCategories The list of [Category] entities scheduled to be deleted.
     * @param futureParentMap A map representing the future parent-child relationships (Key: Child ID, Value: Parent ID).
     * @throws ContentriaException If a violation of referential integrity is detected.
     */
    fun validateReferentialIntegrity(
        deletingCategories: List<Category>,
        futureParentMap: Map<String, String?>
    ) {
        // 1. Extract IDs of categories to be deleted
        val deletingIds = deletingCategories.map { it.id.toString() }.toSet()

        // 2. Validate the target state
        // Check if any category in the new structure references a deleted category as its parent.
        val hasInvalidReference = futureParentMap.values.any { parentId ->
            parentId != null && parentId in deletingIds
        }

        if (hasInvalidReference) {
            throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
        }
    }
}