package com.contentria.api.user.application

import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.user.application.dto.UserInfo
import com.contentria.api.user.domain.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val nicknameGenerator: NicknameGenerator
) {
    @Transactional(readOnly = true)
    fun getActiveUserInfo(userId: UUID): UserInfo {
        val user = userRepository.findActiveById(userId)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        return UserInfo.from(user)
    }

    @Transactional(readOnly = true)
    fun getActiveUserInfo(email: String): UserInfo {
        val user = userRepository.findByEmail(email)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        return UserInfo.from(user)
    }

    @Transactional(readOnly = true)
    fun getUserInfo(email: String): UserInfo {
        val user = userRepository.findByEmail(email)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        return UserInfo.from(user)
    }

    @Transactional
    fun upsertSocialUser(email: String, name: String, pictureUrl: String?): UserInfo {
        return userRepository.findByEmail(email)?.let { existingUser ->
            var updated = false
            if (existingUser.realUsername != name) {
                existingUser.realUsername = name
                updated = true
            }
            if (existingUser.pictureUrl != pictureUrl) {
                existingUser.pictureUrl = pictureUrl
                updated = true
            }
            if (existingUser.status.isUnverified()) {
                existingUser.status = UserStatus.ACTIVE
                updated = true
            }

            if (updated) {
                log.info { "Updated social user profile: $email" }
            }

            UserInfo.from(existingUser)
        } ?: run {
            log.info { "Creating new social user: $email" }
            val newUser = User.createSocialUser(
                email = email,
                realUsername = name,
                username = name,
                nickname = generateUniqueNickname(),
                pictureUrl = pictureUrl
            )

            val userRole = roleRepository.findByName(Role.USER)
                ?: throw ContentriaException(ErrorCode.REQUIRED_ROLE_NOT_FOUND)

            newUser.addRole(userRole)

            UserInfo.from(userRepository.save(newUser))
        }
    }

    @Transactional
    fun createUnverifiedUser(email: String, name: String): UserInfo {
        userRepository.findByEmail(email)?.let { user ->
            if (user.status.isActive()) {
                throw ContentriaException(ErrorCode.ALREADY_EXISTS_EMAIL)
            }

            userRepository.delete(user)
            userRepository.flush()
        }

        val newUser = User.createEmailUser(
            email = email,
            username = name,
            nickname = generateUniqueNickname()
        )

        val defaultRole = roleRepository.findByName(Role.USER)
            ?: throw ContentriaException(ErrorCode.REQUIRED_ROLE_NOT_FOUND)

        newUser.addRole(defaultRole)

        val savedUser = userRepository.save(newUser)
        return UserInfo.from(savedUser)
    }

    @Transactional
    fun activateUserByEmail(email: String): UserInfo {
        val user = userRepository.findByEmail(email)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        user.status = UserStatus.ACTIVE

        return UserInfo.from(user)
    }

    @Transactional
    fun updateNickname(userId: UUID, newNickname: String): UserInfo {
        val user = userRepository.findActiveById(userId)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        user.nickname = newNickname

        return UserInfo.from(user)
    }

    private fun generateUniqueNickname(): String {
        var nickname: String
        var attempts = 0
        val MAX_ATTEMPTS = 5

        do {
            if (attempts >= MAX_ATTEMPTS) {
                throw ContentriaException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
            nickname = nicknameGenerator.generate()
            attempts++
        } while (userRepository.existsByNickname(nickname))

        return nickname
    }
}