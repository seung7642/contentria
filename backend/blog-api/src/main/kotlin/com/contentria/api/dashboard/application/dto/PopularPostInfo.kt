package com.contentria.api.dashboard.application.dto

data class PopularPostInfo(
    val id: String,
    val title: String,
    val slug: String,
    val views: Long
)
