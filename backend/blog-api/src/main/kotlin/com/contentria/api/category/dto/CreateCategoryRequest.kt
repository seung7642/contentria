package com.contentria.api.category.dto

import jakarta.validation.constraints.NotBlank
import java.util.UUID

class CreateCategoryRequest(
    @field:NotBlank(message = "Category name must not be blank")
    val name: String,
    val parentId: UUID? = null
) {
    fun toCommand(): CreateCategoryCommand {
        return CreateCategoryCommand(
            name = this.name,
            parentId = this.parentId
        )
    }
}