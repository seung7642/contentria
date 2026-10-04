package com.contentria.api.global.config

import com.contentria.common.global.config.R2Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

/** S3Client itself comes from blog-common's R2Config; only the presigner is api-specific. */
@Configuration
class R2Config(
    private val r2Properties: R2Properties
) {

    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .endpointOverride(URI.create(r2Properties.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(r2Properties.accessKeyId, r2Properties.secretAccessKey)
                )
            )
            .region(Region.of("auto"))
            .build()
    }
}
