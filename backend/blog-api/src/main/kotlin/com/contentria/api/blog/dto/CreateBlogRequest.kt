package com.contentria.api.blog.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateBlogRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 3, max = 30, message = "Name must have between 3 and 30 characters")
    @field:Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "Slug can only contain lowercase letters, numbers, and hyphens",
    )
    val slug: String,
)