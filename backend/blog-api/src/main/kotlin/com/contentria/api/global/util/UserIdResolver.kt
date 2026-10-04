package com.contentria.api.global.util

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("userIdResolver")
class UserIdResolver {

    fun getUserId(request: HttpServletRequest): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return request.remoteAddr ?: "unknown"

        val principal = authentication.principal
        if (principal is AuthUserDetails) {
            return principal.userId.toString()
        }

        return request.remoteAddr ?: "unknown"
    }
}
