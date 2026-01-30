package com.contentria.api.analytics.application.dto

data class PopularPostStatInfo(
    val postId: String,
    val title: String,
    val viewCount: Long
)
