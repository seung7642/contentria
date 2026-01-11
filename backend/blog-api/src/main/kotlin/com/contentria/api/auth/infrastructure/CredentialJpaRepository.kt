package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.domain.Credential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CredentialJpaRepository : JpaRepository<Credential, UUID> {

    fun findByEmail(email: String): Credential?
}