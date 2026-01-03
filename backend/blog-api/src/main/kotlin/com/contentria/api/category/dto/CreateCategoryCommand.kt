package com.contentria.api.category.dto

import jakarta.validation.constraints.NotBlank
import java.util.UUID

class CreateCategoryCommand(
    val name: String,
    val parentId: UUID? = null
)