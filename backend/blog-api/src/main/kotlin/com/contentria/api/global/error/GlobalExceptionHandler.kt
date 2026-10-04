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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

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

    /**
     * 경로/쿼리 파라미터 타입 변환 실패 (예: {postId} 자리에 UUID가 아닌 문자열).
     * 클라이언트가 잘못된 주소를 친 것이므로 500/ERROR(스택트레이스)가 아니라
     * 400/warn 한 줄로 처리한다 — 운영 알림 노이즈 방지.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        e: MethodArgumentTypeMismatchException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val requestUri = (request as ServletWebRequest).request.requestURI
        log.warn { "Type mismatch for parameter '${e.name}' (value=${e.value}) on path $requestUri" }

        val errorCode = ErrorCode.INVALID_INPUT_VALUE

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            error = errorCode.status.reasonPhrase,
            message = errorCode.message,
            path = requestUri,
            code = errorCode.code,
            details = mapOf(e.name to "invalid value: ${e.value}")
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
        val fieldErrors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalid") }

        val errorResponse = ErrorResponse(
            status = errorCode.status.value(),
            error = errorCode.status.reasonPhrase,
            message = errorCode.message,
            path = requestUri,
            code = errorCode.code,
            details = fieldErrors
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