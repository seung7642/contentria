package com.contentria.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.markdown")
data class MarkdownProperties(
    val allowExternalLinks: Boolean = true,
    /**
     * 허용할 이미지 프로토콜 목록
     */
    val allowedImageProtocols: List<String> = listOf("http", "https", "data"),
    /**
     * 외부 링크에 rel="nofollow" 속성 추가 여부 (보안)
     */
    val addRelNofollow: Boolean = true,
    /**
     * Markdown content 의 최대 길이 (DoS 방지 목적)
     */
    val maxContentLength: Int = 1_000_000
)
