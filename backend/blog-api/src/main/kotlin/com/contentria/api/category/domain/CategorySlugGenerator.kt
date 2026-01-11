package com.contentria.api.category.domain

import com.contentria.api.global.util.SlugUtils
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CategorySlugGenerator(
    private val categoryRepository: CategoryRepository
) {
    fun generate(blogId: UUID, name: String): String {
        val baseSlug = SlugUtils.toSlug(name)

        val existingSlugs = categoryRepository.findSimilarSlugs(blogId, baseSlug)

        if (existingSlugs.isEmpty()) {
            return baseSlug
        }

        val maxSuffix = existingSlugs.asSequence()
            .mapNotNull { slug ->
                if (slug == baseSlug) {
                    0
                } else {
                    slug.substringAfter("$baseSlug-").toIntOrNull()
                }
            }
            .maxOrNull() ?: 0

        return "$baseSlug-${maxSuffix + 1}"
    }
}