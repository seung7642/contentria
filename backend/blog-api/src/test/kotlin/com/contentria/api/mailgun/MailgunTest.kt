package com.contentria.api.mailgun

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.mailgun.model.message.Message
import feign.FeignException
import feign.Request
import feign.Retryer
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

class MailgunTest {

//    @Test
    fun test() {
        val MAILGUN_API_KEY = "9da866ed0430bb91287c6365c06b5c9a-a908eefc-54f1ba51"


        val EMAIL_FROM = "Contentria <no-reply@contentria.com>"
        val USER_EMAIL = "seung7642@naver.com"

        val message = Message.builder()
            .from(EMAIL_FROM)
            .to(USER_EMAIL)
            .subject("Verify your email address")
//            .text("Verify your email address")
            .template("verify-template")
            .mailgunVariables(mapOf("recipient_email" to "test@contentria.com", "verification_code" to "523943", "app_name" to "Contentria"))
            .build()

        val mailgunMessagesApi = MailgunClient.config(MAILGUN_API_KEY)
            .retryer(Retryer.Default())
            .options(Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
            .createApi(MailgunMessagesApi::class.java)

        val MY_DOMAIN = "contentria.com"
        try {
            val messageResponse = mailgunMessagesApi.sendMessage(MY_DOMAIN, message)
        } catch (e: FeignException) {
            logger.error(e) { "Failed to send email: ${e.message}" }
        }
    }

}