package com.contentria.api.user.repository

import com.contentria.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.email = :email")
    fun findByEmailWithRoles(@Param("email") email: String): User?
}