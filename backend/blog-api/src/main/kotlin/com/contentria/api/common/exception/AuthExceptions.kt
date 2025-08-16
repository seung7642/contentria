package com.contentria.api.common.exception

import org.springframework.http.HttpStatus

open class AuthException(message: String, val status: HttpStatus, cause: Throwable? = null) :
    RuntimeException(message, cause)

class RecaptchaVerificationFailedException(details: String?) :
    AuthException("reCAPTCHA verification failed: ${details ?: "Unknown reason"}", HttpStatus.FORBIDDEN)

class RecaptchaScoreTooLowException(score: Double?, action: String?) :
    AuthException("reCAPTCHA score $score for action '$action' is too low.", HttpStatus.FORBIDDEN)

class MissingRecaptchaTokenException(tokenType: String) :
    AuthException("Required reCAPTCHA $tokenType token is missing.", HttpStatus.FORBIDDEN)

class UserAlreadyVerifiedException(email: String) :
    AuthException("User with email $email already exists and is verified.", HttpStatus.FORBIDDEN)

class InvalidInputException(message: String) : AuthException(message, HttpStatus.BAD_REQUEST)

class VerificationCodeSendingException(email: String, cause: Throwable?) :
    AuthException("Failed to send verification code to $email.", HttpStatus.INTERNAL_SERVER_ERROR)

class InvalidVerificationCodeException(message: String = "Invalid or expired verification code.") :
    AuthException(message, HttpStatus.BAD_REQUEST)