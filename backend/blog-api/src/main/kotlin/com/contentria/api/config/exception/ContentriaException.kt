package com.contentria.api.config.exception

class ContentriaException(
    val errorCode: ErrorCode,
    val details: Map<String, String>? = null
) : RuntimeException(errorCode.message)