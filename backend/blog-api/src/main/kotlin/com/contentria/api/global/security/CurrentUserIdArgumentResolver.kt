package com.contentria.api.global.security

import com.contentria.api.auth.security.CustomUserDetails
import com.contentria.api.global.annotation.CurrentUserId
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

@Component
class CurrentUserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUserId::class.java) &&
                parameter.parameterType == UUID::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): UUID {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            throw ContentriaException(ErrorCode.UNEXPECTED_AUTHENTICATION_PRINCIPAL)
        }

        val principal = authentication.principal
        if (principal is CustomUserDetails) {
            return principal.userId
                ?: throw ContentriaException(ErrorCode.UNEXPECTED_AUTHENTICATION_PRINCIPAL)
        }

        throw ContentriaException(ErrorCode.UNEXPECTED_AUTHENTICATION_PRINCIPAL)
    }
}