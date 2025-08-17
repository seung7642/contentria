package com.contentria.api.post

enum class PostStatus {
    DRAFT,        // 임시저장
    PUBLISHED,    // 발행됨
    PRIVATE,      // 비공개
    ARCHIVED      // 보관 (나만보기)
}