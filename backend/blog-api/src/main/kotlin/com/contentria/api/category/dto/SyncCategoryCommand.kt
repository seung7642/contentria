package com.contentria.api.category.dto

class SyncCategoryCommand(
    val id: String,
    val name: String,
    val parentId: String? = null,
    val order: Int
)