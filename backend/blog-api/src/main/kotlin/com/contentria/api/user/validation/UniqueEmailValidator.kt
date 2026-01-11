package com.contentria.api.user.validation

import com.contentria.api.user.infrastructure.UserJpaRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UniqueEmailValidator(
    private val userJpaRepository: UserJpaRepository
) : ConstraintValidator<UniqueEmail, String> {

    override fun isValid(
        email: String?,
        context: ConstraintValidatorContext?
    ): Boolean {
        if (email == null) {
            return true
        }
        return !userJpaRepository.existsByEmail(email)
    }
}