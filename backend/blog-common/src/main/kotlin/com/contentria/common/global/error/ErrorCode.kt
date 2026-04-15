package com.contentria.common.global.error

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
    RECAPTCHA_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C0007", "Error occurred while verifying reCAPTCHA token."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "C0008", "Too many requests. Please try again later."),

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
    AUTHENTICATED_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AU0006", "User associated with the token not found. Please log in again."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AU0007", "Authentication is required to access this resource."),
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "AU0008", "You do not have permission to access this resource."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AU0009", "Invalid email or password."),
    USER_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "AU0010", "User account is not active. Please verify your email or contact support."),
    REQUIRED_ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "AU0011", "Required role not found in the system. Please contact support."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "US0000", "User not found."),

    // Blog
    DUPLICATE_BLOG_SLUG(HttpStatus.CONFLICT, "BL0000", "Blog slug already in use. Please choose a different one."),
    NOT_FOUND_BLOG(HttpStatus.NOT_FOUND, "BL0001", "Not found blog. Please contact support."),
    FORBIDDEN_ACCESS_BLOG(HttpStatus.FORBIDDEN, "BL0002", "You do not have permission to access this blog."),
    NOT_ALLOWED_BLOG_NAME(HttpStatus.BAD_REQUEST, "BL0003", "Not allowed blog name. Please choose a different one."),

    // Post
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "PO0000", "Not found post. Please contact support."),

    // Category
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "CA0000", "Not found category. Please contact support."),
    MAX_CATEGORY_LEVEL_EXCEEDED(HttpStatus.BAD_REQUEST, "CA0001", "Maximum category nesting level exceeded."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "CA0002", "Category name already exists under the same parent."),
    CANNOT_DELETE_CATEGORY(HttpStatus.BAD_REQUEST, "CA0003", "Cannot delete category that has associated posts."),

    // Media
    NOT_FOUND_MEDIA(HttpStatus.NOT_FOUND, "ME0000", "Media not found."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.BAD_REQUEST, "ME0001", "Unsupported file type. Allowed: JPEG, PNG, WebP, GIF."),
    MEDIA_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "ME0002", "File size exceeds the maximum allowed limit."),
    MEDIA_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "ME0003", "You do not have permission to delete this media."),
    MEDIA_DAILY_UPLOAD_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "ME0004", "Daily upload quota exceeded. Please try again tomorrow."),
    MEDIA_POST_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "ME0005", "Maximum number of images per post exceeded."),
    MEDIA_CONTENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ME0006", "Uploaded file content does not match the declared content type."),

    // Markdown
    MARKDOWN_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MD0000", "An error occurred while processing the markdown content."),
    EMPTY_MARKDOWN_CONTENT(HttpStatus.BAD_REQUEST, "MD0001", "Markdown content cannot be empty."),
    MARKDOWN_CONTENT_TOO_LARGE(HttpStatus.BAD_REQUEST, "MD0002", "Markdown content exceeds the maximum allowed length."),

    // Mail
    MAIL_SENDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MA0000", "Failed to send email. Please try again later.")
    ;
}