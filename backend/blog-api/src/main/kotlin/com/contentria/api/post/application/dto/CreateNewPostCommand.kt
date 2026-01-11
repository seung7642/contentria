package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.PostStatus
import java.util.UUID

// 서비스 계층의 '생성', '수정'을 담당하는 메서드의 입력 객체의 네이밍은 접미사로 'Command' 를 사용한다.
data class CreateNewPostCommand(
    val blogId: UUID,
    val title: String,
    val contentMarkdown: String,
    val status: PostStatus,
    val categoryId: UUID
)