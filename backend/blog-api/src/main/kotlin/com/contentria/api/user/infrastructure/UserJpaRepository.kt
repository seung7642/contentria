package com.contentria.api.user.infrastructure

import com.contentria.api.user.domain.User
import com.contentria.api.user.domain.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserJpaRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.email = :email")
    fun findByEmailWithRoles(@Param("email") email: String): User?

    fun findByIdAndStatus(id: UUID, status: UserStatus): User?

    fun findByNickname(nickname: String): User?

    fun existsByNickname(nickname: String): Boolean

    fun existsByEmail(email: String): Boolean
}