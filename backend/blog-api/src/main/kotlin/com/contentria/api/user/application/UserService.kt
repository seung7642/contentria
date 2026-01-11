package com.contentria.api.user.application

import com.contentria.api.blog.domain.BlogRepository
import com.contentria.api.blog.dto.BlogSummary
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.user.controller.dto.CurrentUserResponse
import com.contentria.api.user.domain.Role
import com.contentria.api.user.domain.RoleRepository
import com.contentria.api.user.domain.User
import com.contentria.api.user.domain.UserRepository
import com.contentria.api.user.domain.UserStatus
import com.contentria.api.user.application.dto.UserSummaryInfo
import com.contentria.api.user.security.GoogleUserInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val blogRepository: BlogRepository,
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
                ?: throw ContentriaException(ErrorCode.REQUIRED_ROLE_NOT_FOUND)

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
                throw ContentriaException(
                    ErrorCode.INTERNAL_SERVER_ERROR
                )
            }
        }
    }

    @Transactional
    fun createUnverifiedUser(email: String, name: String, password: String?): User {
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
        )

        val defaultRole = roleRepository.findByName(Role.USER)
            ?: throw ContentriaException(ErrorCode.REQUIRED_ROLE_NOT_FOUND)

        newUser.addRole(defaultRole)

        return userRepository.save(newUser)
    }

    @Transactional
    fun activateUserByEmail(email: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        user.status = UserStatus.ACTIVE

        return user
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun findActiveUserById(userId: UUID): User {
        val user = userRepository.findActiveById(userId)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        return user
    }

    @Transactional(readOnly = true)
    fun getCurrentUserInfo(userId: UUID): CurrentUserResponse {
        val user = findActiveUserById(userId)

        val blogs = blogRepository.findAllByUserId(userId)

        return CurrentUserResponse(
            userId = user.id,
            email = user.email,
            name = user.realUsername,
            profileImage = user.pictureUrl,
            blogs = blogs.map { BlogSummary.from(it) }
        )
    }

    @Transactional(readOnly = true)
    fun getUserSummary(userId: UUID): UserSummaryInfo {
        val user = userRepository.findById(userId)
        return UserSummaryInfo(
            username = user?.username!!,
            pictureUrl = user.pictureUrl
        )
    }
}