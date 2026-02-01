package com.contentria.common.infrastructure.mail

import com.contentria.common.global.config.CommonProperties
import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import feign.Request
import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class MailgunConfig {

    @Bean
    fun mailgunMessageApi(commonProperties: CommonProperties): MailgunMessagesApi {
        return MailgunClient.config(commonProperties.mail.mailgun.apiKey)
            .retryer(Retryer.Default())
            .options(Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
            .createApi(MailgunMessagesApi::class.java)
    }
}