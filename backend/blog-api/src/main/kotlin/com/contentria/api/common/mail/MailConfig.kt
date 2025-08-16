package com.contentria.api.common.mail

import com.contentria.api.common.properties.AppProperties
import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import feign.Request
import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class MailConfig(
    private val appProperties: AppProperties
) {

    @Bean
    fun mailgunMessageApi(): MailgunMessagesApi {
        val mailgunMessagesApiUS = MailgunClient.config(appProperties.mail.mailgun.apiKey)
            .retryer(Retryer.Default())
            .options(Request.Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
            .createApi(MailgunMessagesApi::class.java)
        return mailgunMessagesApiUS
    }
}