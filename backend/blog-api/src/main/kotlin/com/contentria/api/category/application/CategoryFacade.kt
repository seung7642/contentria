package com.contentria.api.category.application

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.application.PostService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CategoryFacade(
    private val categoryService: CategoryService,
    private val postService: PostService
) {
    fun deleteCategory(categoryId: UUID) {
        if (postService.existsByCategoryId(categoryId)) {
            throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
        }

//        categoryService.delete(categoryId)
    }
}