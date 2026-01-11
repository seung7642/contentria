package com.contentria.api.auth.domain

import java.util.UUID

interface CredentialRepository {

    fun findById(id: UUID): Credential?
    fun save(credential: Credential): Credential
    fun delete(credential: Credential)
    fun deleteAll(credentials: List<Credential>)

    fun findByEmail(email: String): Credential?
}