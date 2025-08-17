package com.contentria.common.mail

import com.contentria.common.properties.CommonProperties
import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MailService(
    private val mailgunMessagesApi: MailgunMessagesApi,
    private val commonProperties: CommonProperties
) {

    fun send() {
        val USER_EMAIL = "seung7642@naver.com"

        val message = Message.builder()
            .from(commonProperties.mail.mailgun.fromAddress)
            .to(USER_EMAIL)
            .subject("Test Subject")
            .text("Test Body")
            .build()

        try {
            val messageResponse = mailgunMessagesApi.sendMessage(commonProperties.mail.mailgun.domain, message)
        } catch (e: FeignException) {
            logger.error(e) { "Failed to send email: ${e.message}" }
        }
    }

    fun sendVerificationEmail(toEmail: String, username: String, code: String) {
        return
    }
}