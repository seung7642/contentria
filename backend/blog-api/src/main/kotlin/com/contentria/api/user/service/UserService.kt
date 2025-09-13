package com.contentria.api.user.service

import com.contentria.api.user.domain.Role
import com.contentria.api.user.domain.User
import com.contentria.api.user.domain.UserStatus
import com.contentria.api.user.security.GoogleUserInfo
import com.contentria.api.user.repository.RoleRepository
import com.contentria.api.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun upsertGoogleUser(googleUserInfo: GoogleUserInfo): User {
        return userRepository.findByEmail(googleUserInfo.email)?.let { existingUser ->
            var updated = false
            if (existingUser.realUsername != googleUserInfo.name) {
                existingUser.realUsername = googleUserInfo.name
                updated = true
            }
            if (existingUser.pictureUrl != googleUserInfo.picture) {
                existingUser.pictureUrl = googleUserInfo.picture
                updated = true
            }
            if (updated) {
                logger.info { "Updating existing user info for email [${existingUser.email}]" }
            } else{
                logger.debug { "Existing user found for email [${existingUser.email}]. No updates needed." }
            }
            existingUser
        } ?: run {
            logger.info { "Creating a new user for Google account email [${googleUserInfo.email}]" }
            val userRole = roleRepository.findByName("ROLE_USER")
                ?: throw IllegalArgumentException("Required role 'ROLE_USER' not found in the database. Please ensure it is initialized.")

            val newUser = User.createGoogleUser(
                email = googleUserInfo.email,
                realUsername = googleUserInfo.name,
                username = googleUserInfo.name,
                pictureUrl = googleUserInfo.picture,
                providerId = googleUserInfo.id
            )

            newUser.userRoles.clear()
            newUser.addRole(userRole)

            try {
                val savedUser = userRepository.save(newUser)
                logger.info { "Successfully saved new user with email [${newUser.email}]" }
                savedUser
            } catch (e: Exception) {
                logger.error(e) { "Failed to save new Google user with email [${googleUserInfo.email}]: ${e.message}" }
                throw RuntimeException("Failed to save new Google user with email [${googleUserInfo.email}]", e)
            }
        }
    }

    @Transactional
    fun createUnverifiedUser(email: String, name: String, password: String?): User {
        // 1. 이미 ACTIVE 상태인 사용자가 있는지 확인
        userRepository.findByEmail(email)?.let { user ->
            if (user.status == UserStatus.ACTIVE) {
                throw IllegalStateException("This email is already in use.")
            }
            // 기존에 UNVERIFIED 레코드가 있다면 삭제 후 새로 생성
            userRepository.delete(user)
        }

        // 2. 비밀번호 해싱 (제공된 경우)
        val hashedPassword = password?.let { passwordEncoder.encode(it) }

        // 3. User 엔티티 생성
        val newUser = User.createEmailUser(
            email = email,
            username = name,
            password = hashedPassword ?: ""
        )

        // 4. 기본 역할(ROLE_USER) 할당
        val defaultRole = roleRepository.findByName(Role.USER)
            ?: throw IllegalArgumentException("Required role 'ROLE_USER' not found in the database.")

        newUser.addRole(defaultRole)

        return userRepository.save(newUser)
    }

    @Transactional
    fun activateUserByEmail(email: String): User {
        // 1. 이메일로 사용자를 찾는다. 없으면 예외를 던진다.
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User with email $email not found.")

        // 2. 이미 ACTIVE 상태이너가 다른 상태인지 확인한다.
        if (user.status != UserStatus.UNVERIFIED) {
            throw IllegalStateException("This user account has already been verified or is in an invalid state.")
        }

        user.status = UserStatus.ACTIVE

        return user
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}