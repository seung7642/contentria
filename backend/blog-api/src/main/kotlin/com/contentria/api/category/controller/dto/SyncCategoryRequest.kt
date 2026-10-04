package com.contentria.api.category.controller.dto

import com.contentria.api.category.application.dto.SyncCategoryCommand
import jakarta.validation.constraints.NotBlank
import java.util.UUID

class SyncCategoryRequest(
    val id: String,

    @field:NotBlank(message = "Category name must not be blank")
    val name: String,

    val parentId: String? = null,
    val order: Int
) {
    fun toCommand(): SyncCategoryCommand {
        return SyncCategoryCommand(
            id = this.id,
            name = this.name,
            parentId = this.parentId,
            order = this.order
        )
    }
}