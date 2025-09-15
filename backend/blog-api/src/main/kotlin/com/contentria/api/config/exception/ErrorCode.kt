package com.contentria.api.config.exception

import jakarta.validation.Payload
import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) : Payload {
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C0000", "Internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C0001", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C0002", "Method not allowed"),
    CLIENT_IP_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "C0003", "Could not determine client IP address."),
    RECAPTCHA_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "C0004", "reCAPTCHA token is missing from the request."),
    RECAPTCHA_VERIFICATION_FAILED(HttpStatus.FORBIDDEN, "C0005", "reCAPTCHA verification failed. Please try again."),
    RECAPTCHA_V3_ACTION_MISMATCH(HttpStatus.BAD_REQUEST, "C0006", "reCAPTCHA action mismatch."),

    // Sign Up
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "SU0000", "Invalid or expired verification code"),
    ALREADY_EXISTS_EMAIL(HttpStatus.CONFLICT, "SU0001", "This email is already in use."),

    // Auth
    UNEXPECTED_AUTHENTICATION_PRINCIPAL(HttpStatus.INTERNAL_SERVER_ERROR, "AU0000", "Could not resolve user details from the current authentication session."),
    OIDC_INVALID_PRINCIPAL(HttpStatus.INTERNAL_SERVER_ERROR, "AU0001", "Invalid authentication principal type during OIDC processing."),
    OIDC_MISSING_EMAIL(HttpStatus.BAD_REQUEST, "AU0002", "Email not found in OIDC provider claims. Please ensure your provider account has a public email."),
    OIDC_POST_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AU0003", "An internal server error occurred after OIDC authentication."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AU0004", "Refresh token not found. Please log in again."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AU0005", "Refresh token has expired. Please log in again."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AU0006", "User associated with the token not found. Please log in again."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AU0007", "Authentication is required to access this resource."),
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "AU0008", "You do not have permission to access this resource."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AU0009", "Invalid email or password."),
    USER_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "AU0010", "User account is not active. Please verify your email or contact support."),
    REQUIRED_ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "AU0011", "Required role not found in the system. Please contact support."),

    // Blog
    DUPLICATE_BLOG_SLUG(HttpStatus.CONFLICT, "BL0000", "Blog slug already in use. Please choose a different one."),
}