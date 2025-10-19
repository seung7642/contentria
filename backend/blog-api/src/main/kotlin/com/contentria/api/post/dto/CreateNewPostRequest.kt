package com.contentria.api.post.dto

import com.contentria.api.post.domain.PostStatus
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID
import org.hibernate.validator.constraints.UUID as ValidUUID

data class CreateNewPostRequest(
    @field:NotNull(message = "사용자 ID는 필수입니다.")
    @field:ValidUUID(message = "올바른 UUID 형식이 아닙니다.")
    val userId: UUID,

    @field:NotNull(message = "제목은 필수입니다.")
    @field:Size(min = 1, max = 150, message = "제목은 1자 이상 150자 이하여야 합니다.")
    val title: String,

    @field:NotNull(message = "내용은 필수입니다.")
    @field:Size(min = 1, max = 100000, message = "내용은 1자 이상 100000자 이하여야 합니다.")
    val contentMarkdown: String,

    @field:NotNull(message = "게시 상태는 필수입니다.")
    val status: PostStatus,

    val categoryId: UUID? = null
) {
    fun toCommand(): CreateNewPostCommand {
        return CreateNewPostCommand(
            userId = this.userId,
            title = this.title,
            contentMarkdown = this.contentMarkdown,
            status = this.status,
            categoryId = this.categoryId
        )
    }
}
