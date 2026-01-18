package com.contentria.api.category.application

import com.contentria.api.blog.application.BlogService
import com.contentria.api.category.application.dto.CategoryInfo
import org.springframework.stereotype.Component
import java.util.*

@Component
class CategoryFacade(
    private val categoryService: CategoryService,
    private val blogService: BlogService
) {
    fun getCategories(userId: UUID, blogId: UUID): List<CategoryInfo> {
        blogService.validateBlogOwner(blogId, userId)

        return categoryService.getFlattenedCategories(blogId)
    }
}