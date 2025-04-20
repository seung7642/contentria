package com.demo.blog.user.repository

import com.demo.blog.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.username = :username")
    fun findByUsernameWithRoles(@Param("username") username: String): User?
}