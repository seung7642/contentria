package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.PostStatus
import java.util.UUID

data class UpdatePostCommand(
    val postId: UUID,
    val blogId: UUID,
    val title: String,
    val contentMarkdown: String,
    val status: PostStatus,
    val categoryId: UUID
)
