package com.contentria.api.post.controller.dto

import com.contentria.api.post.application.dto.UpdatePostCommand
import com.contentria.api.post.domain.PostStatus
import jakarta.validation.constraints.Size
import java.util.UUID

data class UpdatePostRequest(
    val postId: UUID,
    val blogId: UUID,

    @field:Size(min = 1, max = 150, message = "제목은 1자 이상 150자 이하여야 합니다.")
    val title: String,

    @field:Size(min = 1, max = 100000, message = "내용은 1자 이상 100000자 이하여야 합니다.")
    val contentMarkdown: String,

    val status: PostStatus,
    val categoryId: UUID
) {
        fun toCommand(): UpdatePostCommand {
            return UpdatePostCommand(
                postId = this.postId,
                blogId = this.blogId,
                title = this.title,
                contentMarkdown = this.contentMarkdown,
                status = this.status,
                categoryId = this.categoryId
            )
        }
}
