package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.PopularPostInfo

data class PopularPostResponse(
    val id: String,
    val title: String,
    val views: Long
) {
    companion object {
        fun from(popularPostInfo: PopularPostInfo): PopularPostResponse {
            return PopularPostResponse(
                id = popularPostInfo.id,
                title = popularPostInfo.title,
                views = popularPostInfo.views
            )
        }
    }
}
