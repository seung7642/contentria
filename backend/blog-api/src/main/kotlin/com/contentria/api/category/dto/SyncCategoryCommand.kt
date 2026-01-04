package com.contentria.api.category.dto

import java.util.UUID

class SyncCategoryCommand(
    val actorUserId: UUID?,
    val id: String,
    val name: String,
    val parentId: String? = null,
    val order: Int
)