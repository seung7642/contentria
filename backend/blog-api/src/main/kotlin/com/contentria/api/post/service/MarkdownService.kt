package com.contentria.api.post.service

import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.MarkdownProperties
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class MarkdownService(
    private val parser: Parser,
    private val renderer: HtmlRenderer,
    private val safelist: Safelist,
    private val properties: MarkdownProperties
) {
    fun convertToHtml(markdown: String): String {
        try {
            validateInput(markdown)

            // 1. 마크다운 -> HTML 변환
            val rawHtml = convertMarkdownToHtml(markdown)

            // 2. HTML 새니타이징
            val sanitizedHtml = sanitizeHtml(rawHtml)

            // 3. 후처리 (보안강화)
            val processedHtml = postProcess(sanitizedHtml)

            return processedHtml
        } catch (e: Exception) {
            throw ContentriaException(ErrorCode.MARKDOWN_PROCESSING_FAILED)
        }
    }

    private fun validateInput(markdown: String) {
        require(markdown.isNotBlank()) {
            throw ContentriaException(ErrorCode.EMPTY_MARKDOWN_CONTENT)
        }

        if (markdown.length > properties.maxContentLength) {
            throw ContentriaException(ErrorCode.MARKDOWN_CONTENT_TOO_LARGE)
        }
    }

    private fun convertMarkdownToHtml(markdown: String): String {
        val document = parser.parse(markdown)
        return renderer.render(document)
    }

    private fun sanitizeHtml(html: String): String {
        return Jsoup.clean(html, safelist)
    }

    private fun postProcess(html: String): String {
        val doc = Jsoup.parse(html)

        // 외부 링크에 보안 속성 추가
        if (properties.allowExternalLinks) {
            doc.addLinkSecurityAttributes()
        }

        // 이미지 최적화 속성 추가
        doc.optimizeImages()

        // 체크박스 비활성화 (읽기 전용)
        doc.disableCheckboxes()

        return doc.body().html()
    }
}

/**
 * 외부 링크에 보안 속성 추가
 */
private fun Document.addLinkSecurityAttributes() {
    select("a[href]").forEach { link ->
        val href = link.attr("href")

        // 외부 링크 판별
        if (href.startsWith("http://") || href.startsWith("https://")) {
            link.attr("target", "_blank")
            link.attr("rel", "noopener noreferrer nofollow")
        }
    }
}

/**
 * 이미지 최적화 속성 추가
 */
private fun Document.optimizeImages() {
    select("img").forEach { img ->
        img.attr("loading", "lazy")

        if (!img.hasAttr("alt")) {
            img.attr("alt", "")
        }
    }
}

/**
 * 체크박스 비활성화 (읽기 전용 모드)
 */
private fun Document.disableCheckboxes() {
    select("input[type=checkbox]").forEach { checkbox ->
        checkbox.attr("disabled", "disabled")
    }
}