package com.contentria.common.domain.analytics

interface PopularPostStatProjection {

    fun getPostId(): String
    fun getTitle(): String
    fun getSlug(): String
    fun getViewCount(): Long
}
