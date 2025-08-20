package com.contentria.api.config.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unhandled exception occurred for request path ${request.requestURI}: ${ex.message}" }

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

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error(ex) { "JWT processing error for path ${request.requestURI}: ${ex.message}" }

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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error { "Validation error for request path ${request.requestURI}: ${ex.message}" }

        // 실패한 필드와 에러 메시지를 map으로 가공
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed for request",
            path = request.requestURI,
            details = errors
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(OidcAuthenticationProcessingException::class)
    fun handleOidcAuthenticationException(
        ex: OidcAuthenticationProcessingException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error(ex) { "OIDC post-authentication error for path ${request.requestURI}: ${ex.message}" }

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