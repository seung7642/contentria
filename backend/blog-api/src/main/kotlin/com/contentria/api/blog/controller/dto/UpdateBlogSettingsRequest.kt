package com.contentria.api.blog.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateBlogSettingsRequest(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(max = 255, message = "Title must be at most 255 characters")
    val title: String,

    @field:Size(max = 500, message = "Description must be at most 500 characters")
    val description: String?,
)
