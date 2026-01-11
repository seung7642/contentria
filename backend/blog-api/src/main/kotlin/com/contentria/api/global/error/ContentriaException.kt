package com.contentria.api.global.error

class ContentriaException(
    val errorCode: ErrorCode,
    val details: Map<String, String>? = null
) : RuntimeException(errorCode.message)