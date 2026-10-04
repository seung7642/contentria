package com.contentria.api.category.application.dto

class SyncCategoryCommand(
    val id: String,
    val name: String,
    val parentId: String? = null,
    val order: Int
)