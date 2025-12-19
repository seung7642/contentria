package com.contentria.api.post.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class MarkdownService(
    private val parser: Parser,
    private val textContentRenderer: TextContentRenderer
) {
    fun extractSummary(markdown: String, maxLength: Int = 150): String {
        if (markdown.isBlank()) {
            return ""
        }

        val document = parser.parse(markdown)
        val plainText = textContentRenderer.render(document).trim()
        return plainText.take(maxLength)
    }
}