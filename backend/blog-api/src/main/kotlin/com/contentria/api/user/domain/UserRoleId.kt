package com.contentria.api.user.domain

import java.io.Serializable
import java.util.UUID

data class UserRoleId(
    var user: UUID? = null,
    var role: UUID? = null,
) : Serializable