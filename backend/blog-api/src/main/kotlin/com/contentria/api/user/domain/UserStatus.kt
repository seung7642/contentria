package com.contentria.api.user.domain

enum class UserStatus {
    UNVERIFIED, // 이메일 인증 전
    ACTIVE,     // 정상 활동
    SUSPENDED,  // 관리자에 의해 정지됨
    DELETED     // 탈퇴 처리됨
}