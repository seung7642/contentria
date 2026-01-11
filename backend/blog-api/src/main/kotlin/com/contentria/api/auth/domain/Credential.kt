package com.contentria.api.auth.domain

import com.contentria.api.user.domain.AuthProvider
import com.contentria.api.user.domain.BaseEntity
import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "credentials")
class Credential(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    var password: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: AuthProvider,

    var providerId: String? = null,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    val userId: UUID
) : BaseEntity() {

    companion object {
        fun createEmailCredential(userId: UUID, email: String, passwordHash: String? = null): Credential {
            return Credential(
                email = email,
                password = passwordHash,
                provider = AuthProvider.EMAIL,
                userId = userId
            )
        }
    }
}