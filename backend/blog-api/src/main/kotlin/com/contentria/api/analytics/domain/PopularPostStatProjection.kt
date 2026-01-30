package com.contentria.api.analytics.domain

interface PopularPostStatProjection {

    fun getPostId(): String
    fun getTitle(): String
    fun getViewCount(): Long
}