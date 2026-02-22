package com.contentria.common.infrastructure.email

import com.contentria.common.config.CommonTestConfiguration
import com.contentria.common.global.config.CommonProperties
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Integration test for the EmailService that sends a real email using the configured Mailgun credentials.
 * This test is designed for manual execution or specific CI/CD pipelines due to its reliance on
 * an external email service and potential costs.
 *
 * It uses the 'real-mail-test' tag, allowing it to be excluded from regular test runs.
 *
 * Pre-requisites for running this test:
 *   1. Ensure environment variables are configured with:
 *     - `MAILGUN_USERNAME`
 *     - `MAILGUN_PASSWORD`
 *     - `TEST_EMAIL_RECIPIENT`
 *   2. If running via Gradle, ensure to use `-DrunRealMailTests=true` or similar argument to include this tagged test.
 *     - e.g., `gradlew test -DrunRealMailTests=true`
 */
@Tag("real-mail-test")
@ActiveProfiles("test")
@SpringBootTest(classes = [CommonTestConfiguration::class])
class EmailServiceIntegrationTest {

    @Autowired
    private lateinit var emailService: EmailService

    @Autowired
    private lateinit var commonProperties: CommonProperties

    @Value("\${test.email.recipient}")
    private lateinit var recipientEmail: String

    @Test
    fun should_send_email_when_valid_credentials_are_provided() {
        val authCode = "000000"

        Assertions.assertThatCode {
            emailService.sendAuthCodeEmail(recipientEmail, authCode)
        }.doesNotThrowAnyException()
    }
}