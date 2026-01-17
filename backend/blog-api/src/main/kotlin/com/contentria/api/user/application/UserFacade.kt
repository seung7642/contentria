package com.contentria.api.user.application

import com.contentria.api.blog.application.BlogService
import com.contentria.api.user.controller.dto.CurrentUserResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class UserFacade(
    private val userService: UserService,
    private val blogService: BlogService
) {
    @Transactional(readOnly = true)
    fun getCurrentUserInfo(userId: UUID): CurrentUserResponse {
        val user = userService.getActiveUserInfo(userId)

        val blogs = blogService.getBlogInfos(userId)

        return CurrentUserResponse.from(user, blogs)
    }
}