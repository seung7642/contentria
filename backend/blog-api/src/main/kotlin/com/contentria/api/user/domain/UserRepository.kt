package com.contentria.api.user.domain

import java.util.*

interface UserRepository {

    fun save(user: User): User
    fun delete(user: User)
    fun deleteAll(users: List<User>)
    fun findById(userId: UUID): User?
    fun flush()

    fun findByEmail(email: String): User?

    fun findByEmailWithRoles(email: String): User?

    fun findActiveById(id: UUID): User?

    fun findByNickname(nickname: String): User?

    fun existsByNickname(nickname: String): Boolean

    fun existsByEmail(email: String): Boolean
}