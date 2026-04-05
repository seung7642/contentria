package com.contentria.api.auth.application

import com.contentria.api.auth.domain.Credential
import com.contentria.api.auth.domain.CredentialRepository
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.user.domain.AuthProvider
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
        val encodedPassword = rawPassword?.takeIf { it.isNotBlank() }?.let {
            passwordEncoder.encode(it)
        }

        val credential = Credential.createEmailCredential(
            userId = userId,
            email = email,
            passwordHash = encodedPassword
        )

        credentialRepository.save(credential)
    }

    @Transactional
    fun upsertSocialCredential(userId: UUID, email: String, provider: AuthProvider, providerId: String) {
        val credential = credentialRepository.findByEmail(email)
            ?: Credential(
                userId = userId,
                email = email,
                provider = provider,
                providerId = providerId,
                password = null
            ).also { credentialRepository.save(it) }

        if (credential.provider != provider || credential.providerId != providerId) {
            credential.provider = provider
            credential.providerId = providerId
        }
    }

    @Transactional(readOnly = true)
    fun authenticate(email: String, rawPassword: String?): Credential {
        val credential = (credentialRepository.findByEmail(email)
            ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS))

        if (rawPassword == null || credential.password == null
            || !passwordEncoder.matches(rawPassword, credential.password)) {
            throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)
        }

        return credential
    }
}