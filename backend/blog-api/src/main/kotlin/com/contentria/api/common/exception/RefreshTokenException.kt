package com.contentria.api.common.exception

open class RefreshTokenException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class RefreshTokenNotFoundException(
    message: String = "Refresh token not found",
    cause: Throwable? = null
) : RefreshTokenException(message, cause)

class RefreshTokenExpiredException(
    message: String = "Refresh token expired",
    cause: Throwable? = null
) : RefreshTokenException(message, cause)