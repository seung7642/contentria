package com.demo.blog.common.mail

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MailService(
    private val mailgunMessagesApi: MailgunMessagesApi
) {

    fun send() {
        val EMAIL_FROM = "no-reply@contentria.com"
        val USER_EMAIL = "seung7642@naver.com"

        val message = Message.builder()
            .from(EMAIL_FROM)
            .to(USER_EMAIL)
            .subject("Test Subject")
            .text("Test Body")
            .build()

        val MY_DOMAIN = "contentria.com"
        try {
            val messageResponse = mailgunMessagesApi.sendMessage(MY_DOMAIN, message)
        } catch (e: FeignException) {
            logger.error(e) { "Failed to send email: ${e.message}" }
        }
    }

    fun sendVerificationEmail(toEmail: String, username: String, code: String) {
        return
    }
}