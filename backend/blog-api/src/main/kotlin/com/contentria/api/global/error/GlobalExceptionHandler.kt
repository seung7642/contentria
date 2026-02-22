package com.contentria.api.global.error

import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(e: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val requestUri = (request as ServletWebRequest).request.requestURI
        log.error(e) { "Unhandled exception for path ${requestUri}: ${e.message}" }

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An internal server error occurred. Please try again later.",
            path = requestUri,
            code = ErrorCode.INTERNAL_SERVER_ERROR.code
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }

    @ExceptionHandler(MissingRequestCookieException::class)
    fun handleMissingCookieException(e: MissingRequestCookieException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val requestUri = (request as ServletWebRequest).request.requestURI
        log.warn { "Required cookie '${e.cookieName}' is missing for path $requestUri" }

        val errorCode = ErrorCode.REFRESH_TOKEN_NOT_FOUND

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            error = errorCode.status.reasonPhrase,
            message = errorCode.message,
            path = requestUri,
            code = errorCode.code
        )
        return ResponseEntity(errorResponse, errorCode.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val requestUri = (request as ServletWebRequest).request.requestURI
        val errorCode = ErrorCode.INVALID_INPUT_VALUE
        val errorMessage = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: errorCode.message

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            error = errorCode.status.reasonPhrase,
            message = errorMessage,
            path = requestUri,
            code = errorCode.code
        )
        return ResponseEntity(errorResponse, errorCode.status)
    }

    @ExceptionHandler(ContentriaException::class)
    fun handleContentriaException(e: ContentriaException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            error = errorCode.status.reasonPhrase,
            message = errorCode.message,
            path = (request as ServletWebRequest).request.requestURI,
            code = errorCode.code,
            details = e.details ?: emptyMap(),
        )

        return ResponseEntity(errorResponse, errorCode.status)
    }
}