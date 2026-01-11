package com.contentria.api.user.infrastructure

import com.contentria.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserJpaRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.email = :email")
    fun findByEmailWithRoles(@Param("email") email: String): User?

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.status = com.contentria.api.user.domain.UserStatus.ACTIVE")
    fun findActiveById(@Param("id") id: UUID): User?

    fun existsByEmail(email: String): Boolean
}