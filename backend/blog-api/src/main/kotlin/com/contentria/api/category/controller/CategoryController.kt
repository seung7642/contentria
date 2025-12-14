package com.contentria.api.category.controller

import com.contentria.api.blog.service.BlogService
import com.contentria.api.category.dto.CategoryResponse
import com.contentria.api.category.service.CategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

    @GetMapping("/dropdown")
    fun getCategoriesForDropdown(@RequestParam blogId: UUID): ResponseEntity<List<CategoryResponse>> {
        val blog = blogService.getBlogByBlogId(blogId)
        val responses = categoryService.getCategoriesForDropdown(blog)
        return ResponseEntity.ok(responses)
    }
}