package com.contentria.api.auth.application

import com.contentria.api.auth.domain.Credential
import com.contentria.api.auth.domain.CredentialRepository
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CredentialService(
    private val credentialRepository: CredentialRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createPasswordCredential(userId: UUID, email: String, rawPassword: String?) {
        if (rawPassword.isNullOrBlank()) {
            throw ContentriaException(ErrorCode.INVALID_INPUT_VALUE)
        }

        val encodedPassword = passwordEncoder.encode(rawPassword)

        val credential = Credential.createEmailCredential(
            userId = userId,
            email = email,
            passwordHash = encodedPassword
        )

        credentialRepository.save(credential)
    }
}