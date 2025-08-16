package com.contentria.api.common.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Unhandled exception occurred for request path ${request.requestURI}: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An internal server error occurred. Please try again later.",
            path = request.requestURI
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    @ExceptionHandler(io.jsonwebtoken.JwtException::class)
    fun handleJwtException(ex: io.jsonwebtoken.JwtException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "JWT processing error for path ${request.requestURI}: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = "Access denied: Invalid or expired token.",
            path = request.requestURI
        )
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }

    @ExceptionHandler(OidcAuthenticationProcessingException::class)
    fun handleOidcAuthenticationException(
        ex: OidcAuthenticationProcessingException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "OIDC post-authentication error for path ${request.requestURI}: ${ex.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An internal server error occurred during OIDC authentication. Please try again later.",
            path = request.requestURI
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }
}

data class ErrorResponse(
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String? = null
)