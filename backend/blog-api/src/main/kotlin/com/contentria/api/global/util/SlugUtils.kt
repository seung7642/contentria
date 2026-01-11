package com.contentria.api.global.util

import java.util.Locale
import java.util.UUID

// 'object'는 싱글톤 패턴을 언어 차원에서 지원하는 키워드이다.
// 실행 시 즉시 객체가 메모리에 로드된다. 'class' 키워드가 설계도라면 'object' 키워드는 설계도이자 실체이다.
object SlugUtils {

    // 허용할 문자 패턴: 영문 소문자, 숫자, 한글, 공백, 하이픈(-)
    // ^ 기호는 'Not'을 의미함. 즉, 이 안에 없는 건 다 제거 대상.
    private val NOT_ALLOWED_CHARS = Regex("[^a-z0-9가-힣\\s-]")
    private val WHITESPACE = Regex("[\\s]+")
    private val HYPHENS = Regex("[-]+")

    fun toSlug(input: String?): String {
        if (input.isNullOrBlank()) {
            return UUID.randomUUID().toString()
        }

        // 1. 앞뒤 공백 제거
        var slug = input.trim().lowercase(Locale.ROOT)

        // 2. 허용되지 않는 특수문자 제거
        slug = NOT_ALLOWED_CHARS.replace(slug, "")

        // 3. 공백을 하이픈(-)으로 치환
        slug = WHITESPACE.replace(slug, "-")

        // 4. 연속된 하이픈을 하나로 축소 (예: "a---b" -> "a-b")
        slug = HYPHENS.replace(slug, "-")

        // 5. 시작이나 끝에 있는 하이픈 제거 (예: "-hello-" -> "hello")
        slug = slug.trim('-')

        // 6. 만약 모든 문자가 제거되어 빈 문자열이 되었다면? (예: 제목이 "!!! ???")
        if (slug.isBlank()) {
            return UUID.randomUUID().toString()
        }

        return slug
    }
}