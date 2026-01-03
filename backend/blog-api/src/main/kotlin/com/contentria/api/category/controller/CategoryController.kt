package com.contentria.api.category.controller

import com.contentria.api.blog.service.BlogService
import com.contentria.api.category.dto.CategoryResponse
import com.contentria.api.category.dto.CreateCategoryRequest
import com.contentria.api.category.dto.CreateCategoryResponse
import com.contentria.api.category.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val blogService: BlogService
) {

    @GetMapping
    fun getCategories(@RequestParam blogId: UUID): ResponseEntity<List<CategoryResponse>> {
        val blog = blogService.getBlogByBlogId(blogId)
        val categories = categoryService.getFlattenedCategories(blog)
        val responses = categories.map { CategoryResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    fun createCategory(
        @PathVariable blogId: UUID,
        @RequestBody @Valid request: CreateCategoryRequest
    ): ResponseEntity<CreateCategoryResponse> {
        val response = categoryService.createCategory(blogId, request.toCommand())
        return ResponseEntity.ok(CreateCategoryResponse.from(response))
    }
}