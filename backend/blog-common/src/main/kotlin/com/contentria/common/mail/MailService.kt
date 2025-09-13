package com.contentria.common.mail

import com.contentria.common.properties.CommonProperties
import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.model.message.Message
import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class MailService(
    private val mailgunMessagesApi: MailgunMessagesApi,
    private val commonProperties: CommonProperties
) {

    fun send(recipientEmail: String, recipientName: String?, verificationCode: String) {
        val templateVariables = mapOf(
            "recipient_name" to recipientName,
            "verification_code" to verificationCode,
            "app_name" to "Contentria"
        )
        val message = Message.builder()
            .from(commonProperties.mail.mailgun.fromAddress)
            .to(recipientEmail)
            .subject("Verify your email address for Contentria")
            .template("verify-template")
            .mailgunVariables(templateVariables)
            .build()

        try {
            val response = mailgunMessagesApi.sendMessage(commonProperties.mail.mailgun.domain, message)
            log.info { "Successfully sent email to $recipientEmail" }
        } catch (e: FeignException) {
            log.error { "Failed to send email: ${e.message}" }
        }
        return
    }
}