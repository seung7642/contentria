package com.contentria.api.global.config

import com.contentria.api.global.properties.AppProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class R2Config(
    private val appProperties: AppProperties
) {

    @Bean
    fun s3Client(): S3Client {
        val r2 = appProperties.r2
        return S3Client.builder()
            .endpointOverride(URI.create(r2.endpoint))
            .credentialsProvider(credentialsProvider())
            .region(Region.of("auto"))
            .forcePathStyle(true)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val r2 = appProperties.r2
        return S3Presigner.builder()
            .endpointOverride(URI.create(r2.endpoint))
            .credentialsProvider(credentialsProvider())
            .region(Region.of("auto"))
            .build()
    }

    private fun credentialsProvider(): StaticCredentialsProvider {
        val r2 = appProperties.r2
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(r2.accessKeyId, r2.secretAccessKey)
        )
    }
}
