package com.contentria.common.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.getProperty
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class EmailConfig(
    private val env: Environment
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()

        // application.yml (또는 환경 변수)에서 프로퍼티를 읽어와 설정
        mailSender.host = env.getProperty("spring.mail.host")
        mailSender.port = env.getProperty<Int>("spring.mail.port") ?: 587
        mailSender.username = env.getProperty("spring.mail.username")
        mailSender.password = env.getProperty("spring.mail.password")

        // 추가 프로퍼티 설정
        val props = mailSender.javaMailProperties
        props["mail.smtp.auth"] = env.getProperty<Boolean>("spring.mail.properties.mail.smtp.auth") ?: true
        props["mail.smtp.starttls.enable"] = env.getProperty<Boolean>("spring.mail.properties.mail.smtp.starttls.enable") ?: true
        props["mail.smtp.starttls.required"] = env.getProperty<Boolean>("spring.mail.properties.mail.smtp.starttls.required") ?: true
        props["mail.smtp.connectiontimeout"] = env.getProperty<Int>("spring.mail.properties.mail.smtp.connectiontimeout") ?: 10000
        props["mail.smtp.timeout"] = env.getProperty<Int>("spring.mail.properties.mail.smtp.timeout") ?: 10000
        props["mail.smtp.writetimeout"] = env.getProperty<Int>("spring.mail.properties.mail.smtp.writetimeout") ?: 10000

        return mailSender
    }
}