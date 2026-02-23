//package com.contentria.common.infrastructure.mail
//
//import com.contentria.common.global.config.CommonProperties
//import com.contentria.common.global.error.ContentriaException
//import com.contentria.common.global.error.ErrorCode
//import com.mailgun.api.v3.MailgunMessagesApi
//import com.mailgun.model.message.Message
//import io.github.oshai.kotlinlogging.KotlinLogging
//import org.springframework.mail.MailSender
//import org.springframework.mail.SimpleMailMessage
//import org.springframework.stereotype.Component
//
//private val log = KotlinLogging.logger {}
//
//@Component
//class MailgunMailSender(
//    private val mailgunMessagesApi: MailgunMessagesApi,
//    private val commonProperties: CommonProperties
//) : MailSender {
//
//    override fun send(simpleMessages: SimpleMailMessage) {
//        try {
//            val message = Message.builder()
//                .from(commonProperties.mail.mailgun.fromAddress)
//                .to(simpleMessages.to?.firstOrNull() ?: throw ContentriaException(ErrorCode.MAIL_SENDING_FAILED))
//                .subject(simpleMessages.subject ?: "")
//                .text(simpleMessages.text ?: "")
//                .build()
//
//            val response = mailgunMessagesApi.sendMessage(commonProperties.mail.mailgun.domain, message)
//            log.info { "Successfully sent email to ${simpleMessages.to?.firstOrNull()}" }
//        } catch (e: Exception) {
//            log.error(e) { "Failed to send email: ${e.message}" }
//            throw ContentriaException(ErrorCode.MAIL_SENDING_FAILED)
//        }
//    }
//
//    override fun send(vararg simpleMessages: SimpleMailMessage?) {
//        simpleMessages.forEach { send(it) }
//    }
//}