package com.contentria.api.video.infrastructure

import com.contentria.api.global.properties.AppProperties
import com.contentria.common.global.config.R2Properties as CommonR2Properties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

private val log = KotlinLogging.logger {}

/**
 * Storage access for the video domain. Intentionally separate from media's
 * `R2StorageClient` so the `video` bounded context does not depend on `media`.
 * Reuses the shared `S3Presigner` bean (see `R2Config`).
 */
@Component
class VideoStorageClient(
    private val s3Presigner: S3Presigner,
    private val appProperties: AppProperties,
    private val commonR2Properties: CommonR2Properties,
) {

    fun generatePresignedPutUrl(key: String, contentType: String, fileSize: Long): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(key)
            .contentType(contentType)
            .contentLength(fileSize)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(appProperties.r2.presignedUrlTtlMinutes))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()
        log.debug { "Generated video presigned URL for key=$key" }
        return presignedUrl
    }
}
