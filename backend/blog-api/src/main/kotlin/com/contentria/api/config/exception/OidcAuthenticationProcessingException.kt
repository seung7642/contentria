package com.contentria.api.config.exception

class OidcAuthenticationProcessingException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)