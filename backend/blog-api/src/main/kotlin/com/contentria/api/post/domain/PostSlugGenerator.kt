package com.contentria.api.post.domain

import com.contentria.api.global.util.SlugUtils
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PostSlugGenerator(
    private val postRepository: PostRepository
) {
    fun generate(blogId: UUID, rawTitle: String): String {
        val baseSlug = SlugUtils.toSlug(rawTitle)
        val existingSlugs = postRepository.findSlugsByPrefix(blogId, baseSlug)

        if (existingSlugs.isEmpty()) {
            return baseSlug
        }

        if (!existingSlugs.contains(baseSlug)) {
            return baseSlug
        }

        val maxSuffix = existingSlugs.asSequence()
            .mapNotNull { slug ->
                val suffix = slug.removePrefix(baseSlug)
                if (suffix.matches(Regex("-\\d+"))) {
                    suffix.substring(1).toLongOrNull()
                } else {
                    null
                }
            }
            .maxOrNull() ?: 0L

        return "$baseSlug-${maxSuffix + 1}"
    }
}