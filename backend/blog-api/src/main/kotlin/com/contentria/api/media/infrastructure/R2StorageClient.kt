package com.contentria.api.media.infrastructure

import com.contentria.api.global.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class R2StorageClient(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val appProperties: AppProperties
) {

    fun generatePresignedPutUrl(storedKey: String, contentType: String, fileSize: Long): String {
        val r2 = appProperties.r2

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(r2.bucketName)
            .key(storedKey)
            .contentType(contentType)
            .contentLength(fileSize)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(r2.presignedUrlTtlMinutes))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()
        log.debug { "Generated presigned URL for key=$storedKey" }
        return presignedUrl
    }

    fun deleteObject(storedKey: String) {
        val r2 = appProperties.r2

        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(r2.bucketName)
            .key(storedKey)
            .build()

        s3Client.deleteObject(deleteRequest)
        log.info { "Deleted R2 object: key=$storedKey" }
    }

    fun getObjectHeadBytes(storedKey: String, numBytes: Int): ByteArray {
        val r2 = appProperties.r2

        val getRequest = GetObjectRequest.builder()
            .bucket(r2.bucketName)
            .key(storedKey)
            .range("bytes=0-${numBytes - 1}")
            .build()

        val response = s3Client.getObjectAsBytes(getRequest)
        return response.asByteArray()
    }

    fun copyObject(sourceKey: String, destinationKey: String) {
        val r2 = appProperties.r2

        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(r2.bucketName)
            .sourceKey(sourceKey)
            .destinationBucket(r2.bucketName)
            .destinationKey(destinationKey)
            .build()

        s3Client.copyObject(copyRequest)
        log.info { "Copied R2 object: sourceKey=$sourceKey, destinationKey=$destinationKey" }
    }
}
