package com.contentria.api.user.repository

import com.contentria.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.email = :email")
    fun findByEmailWithRoles(@Param("email") email: String): User?

    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.status = com.contentria.api.user.domain.UserStatus.ACTIVE")
    fun findActiveById(@Param("id") id: UUID): User?
}