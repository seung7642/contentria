package com.contentria.api.analytics.application.dto

data class PopularPostStatInfo(
    val postId: String,
    val title: String,
    val slug: String,
    val viewCount: Long
)
