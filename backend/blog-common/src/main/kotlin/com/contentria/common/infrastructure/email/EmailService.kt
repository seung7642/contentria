package com.contentria.common.infrastructure.email

import com.contentria.common.global.config.CommonProperties
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

private val log = KotlinLogging.logger {}

/**
 * Service class for sending various types of emails, including HTML emails with dynamic content and images.
 * Utilizes Spring's JavaMailSender and Thymeleaf for template processing.
 *
 * @param mailSender The Spring's JavaMailSender instance for sending emails.
 * @param templateEngine The Thymeleaf template engine for processing HTML email templates.
 * @param commonProperties Application-specific common properties, including mail configuration.
 */
@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val commonProperties: CommonProperties,
    private val applicationContext: ApplicationContext
) {
    /**
     * Sends an HTML email containing a dynamic authentication code and an inline company logo.
     * The email content is generated using a Thymeleaf template located at `src/main/resources/templates/auth-code-email.html`.
     *
     * @param to The recipient's email address.
     * @param authCode The authentication code to be dynamically inserted into the email template.
     * @throws ContentriaException If any error occurs during the email sending process, encapsulating a more specific cause.
     */
    fun sendAuthCodeEmail(to: String, authCode: String) {
        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            val context = Context()
            context.setVariable("recipientEmail", to)
            context.setVariable("otpCode", authCode)
            context.setVariable("appName", "Contentria")

            val htmlContent = templateEngine.process(AUTH_CODE_TEMPLATE_PATH, context)

            helper.setFrom(commonProperties.mail.mailgun.fromAddress)
            helper.setTo(to)
            helper.setSubject("[Contentria] Verify your email")
            helper.setText(htmlContent, true)

            val logoResource: Resource = applicationContext.getResource("classpath:/static/logo.png")
            helper.addInline("logoImage", logoResource)

            mailSender.send(message)
            log.info { "Successfully sent email to $to" }
        } catch (e: Exception) {
            log.error(e) { "Failed to send email to $to: ${e.message}" }
            throw ContentriaException(ErrorCode.MAIL_SENDING_FAILED)
        }
    }

    companion object {
        private const val AUTH_CODE_TEMPLATE_PATH = "auth-code-email"
    }
}