package com.contentria.api.user.infrastructure

import com.contentria.api.user.domain.User
import com.contentria.api.user.domain.UserRepository
import com.contentria.api.user.domain.UserStatus
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun delete(user: User) {
        userJpaRepository.delete(user)
    }

    override fun deleteAll(users: List<User>) {
        userJpaRepository.deleteAll(users)
    }

    override fun flush() {
        userJpaRepository.flush()
    }

    override fun findById(userId: UUID): User? {
        return userJpaRepository.findByIdOrNull(userId)
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)
    }

    override fun findByEmailWithRoles(email: String): User? {
        return userJpaRepository.findByEmailWithRoles(email)
    }

    override fun findActiveById(id: UUID): User? {
        return userJpaRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
    }

    override fun findByNickname(nickname: String): User? {
        return userJpaRepository.findByNickname(nickname)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return userJpaRepository.existsByNickname(nickname)
    }

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }
}