package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.domain.Credential
import com.contentria.api.auth.domain.CredentialRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CredentialRepositoryImpl(
    private val jpaRepository: CredentialJpaRepository
) : CredentialRepository {

    override fun findById(id: UUID): Credential? {
        return jpaRepository.findByIdOrNull(id)
    }

    override fun save(credential: Credential): Credential {
        return jpaRepository.save(credential)
    }

    override fun delete(credential: Credential) {
        jpaRepository.delete(credential)
    }

    override fun deleteAll(credentials: List<Credential>) {
        jpaRepository.deleteAll(credentials)
    }

    override fun findByEmail(email: String): Credential? {
        return jpaRepository.findByEmail(email)
    }
}