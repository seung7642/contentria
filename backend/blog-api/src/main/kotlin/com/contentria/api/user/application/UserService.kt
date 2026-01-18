package com.contentria.api.user.application

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
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
    private val roleRepository: RoleRepository
) {

//    @Transactional
//    fun upsertGoogleUser(googleUserInfo: GoogleUserInfo): User {
//        return userRepository.findByEmail(googleUserInfo.email)?.let { existingUser ->
//            var updated = false
//            if (existingUser.realUsername != googleUserInfo.name) {
//                existingUser.realUsername = googleUserInfo.name
//                updated = true
//            }
//            if (existingUser.pictureUrl != googleUserInfo.picture) {
//                existingUser.pictureUrl = googleUserInfo.picture
//                updated = true
//            }
//            if (updated) {
//                log.info { "Updating existing user info for email [${existingUser.email}]" }
//            } else{
//                log.debug { "Existing user found for email [${existingUser.email}]. No updates needed." }
//            }
//            existingUser
//        } ?: run {
//            log.info { "Creating a new user for Google account email [${googleUserInfo.email}]" }
//            val userRole = roleRepository.findByName("ROLE_USER")
//                ?: throw ContentriaException(ErrorCode.REQUIRED_ROLE_NOT_FOUND)
//
//            val newUser = User.createGoogleUser(
//                email = googleUserInfo.email,
//                realUsername = googleUserInfo.name,
//                username = googleUserInfo.name,
//                pictureUrl = googleUserInfo.picture,
//                providerId = googleUserInfo.id
//            )
//
//            newUser.userRoles.clear()
//            newUser.addRole(userRole)
//
//            try {
//                val savedUser = userRepository.save(newUser)
//                log.info { "Successfully saved new user with email [${newUser.email}]" }
//                savedUser
//            } catch (e: Exception) {
//                log.error(e) { "Failed to save new Google user with email [${googleUserInfo.email}]: ${e.message}" }
//                throw ContentriaException(
//                    ErrorCode.INTERNAL_SERVER_ERROR
//                )
//            }
//        }
//    }

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
            val newUser = User.createSocialUser(email, name, name, pictureUrl)

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
}