package com.contentria.api.blog.application.dto

data class BlogSummaryInfo(
    val title: String,
    val slug: String,
    val description: String
) {
    companion object {
        fun from(blogInfo: BlogInfo): BlogSummaryInfo {
            return BlogSummaryInfo(
                title = blogInfo.title,
                slug = blogInfo.slug,
                description = blogInfo.description
            )
        }
    }
}
