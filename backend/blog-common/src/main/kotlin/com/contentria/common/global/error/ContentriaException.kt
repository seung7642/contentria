package com.contentria.common.global.error

class ContentriaException(
    val errorCode: ErrorCode,
    val details: Map<String, String>? = null
) : RuntimeException(errorCode.message)