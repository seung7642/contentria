package com.contentria.api.category.application

import com.contentria.api.blog.application.BlogService
import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.category.application.dto.SyncCategoryCommand
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.application.PostService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class CategoryFacade(
    private val categoryInternalService: CategoryInternalService,
    private val blogService: BlogService,
    private val postService: PostService
) {
    @Transactional(readOnly = true)
    fun getCategories(userId: UUID, blogId: UUID): List<CategoryInfo> {
        blogService.validateBlogOwner(blogId, userId)

        // 아래 코드 에러. 해당 함수는 CategoryService에 위치
        return categoryInternalService.getFlattenedCategoryInfos(blogId)
    }

    @Transactional
    fun syncCategories(blogId: UUID, userId: UUID, commands: List<SyncCategoryCommand>) {
        blogService.validateBlogOwner(blogId, userId)

        val existingCategories = categoryInternalService.getCategories(blogId)

        val requestIds = commands.map { it.id }.toSet()
        val toDelete = existingCategories.filter { it.id.toString() !in requestIds }

        if (toDelete.isNotEmpty()) {
            val toDeleteIds = toDelete.map { it.id!! }
            if (postService.existsByCategoryIds(toDeleteIds)) {
                throw ContentriaException(ErrorCode.CANNOT_DELETE_CATEGORY)
            }
        }

        categoryInternalService.applySync(blogId, commands, existingCategories)
    }
}