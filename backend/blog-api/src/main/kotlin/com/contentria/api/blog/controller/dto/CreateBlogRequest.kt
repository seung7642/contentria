package com.contentria.api.blog.controller.dto

import com.contentria.api.blog.application.dto.CreateBlogCommand
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
) {
    fun toCommand(): CreateBlogCommand {
        return CreateBlogCommand(
            slug = this.slug,
        )
    }
}