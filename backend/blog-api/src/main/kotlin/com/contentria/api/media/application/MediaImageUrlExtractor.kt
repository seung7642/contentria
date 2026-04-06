package com.contentria.api.media.application

import com.contentria.api.global.properties.AppProperties
import org.springframework.stereotype.Component

@Component
class MediaImageUrlExtractor(
    private val appProperties: AppProperties
) {

    private val markdownImagePattern = Regex("""!\[.*?]\((.*?)\)""")

    fun extractImageUrls(markdown: String): List<String> {
        val cdnBaseUrl = appProperties.r2.publicUrl
        return markdownImagePattern.findAll(markdown)
            .map { it.groupValues[1] }
            .filter { it.startsWith(cdnBaseUrl) }
            .toList()
    }
}
