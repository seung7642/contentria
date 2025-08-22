package com.contentria.api.user.validation

import com.contentria.api.user.repository.UserRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UniqueEmailValidator(
    private val userRepository: UserRepository
) : ConstraintValidator<UniqueEmail, String> {

    override fun isValid(
        email: String?,
        context: ConstraintValidatorContext?
    ): Boolean {
        if (email == null) {
            return true
        }
        return !userRepository.existsByEmail(email)
    }
}