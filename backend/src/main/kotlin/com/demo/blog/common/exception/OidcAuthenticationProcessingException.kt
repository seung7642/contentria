package com.demo.blog.common.exception

class OidcAuthenticationProcessingException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)