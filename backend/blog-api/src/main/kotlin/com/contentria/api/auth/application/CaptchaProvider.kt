package com.contentria.api.auth.application

import com.contentria.api.auth.application.dto.CaptchaCommand

interface CaptchaProvider {

    fun verify(command: CaptchaCommand): Boolean
}