package com.contentria.api.media.infrastructure

import com.contentria.api.global.properties.AppProperties
import com.contentria.common.global.config.R2Properties as CommonR2Properties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
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
    private val appProperties: AppProperties,
    private val commonR2Properties: CommonR2Properties,
) {

    fun generatePresignedPutUrl(storedKey: String, contentType: String, fileSize: Long): String {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(storedKey)
            .contentType(contentType)
            .contentLength(fileSize)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(appProperties.r2.presignedUrlTtlMinutes))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()
        log.debug { "Generated presigned URL for key=$storedKey" }
        return presignedUrl
    }

    fun deleteObject(storedKey: String) {
        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(storedKey)
            .build()

        s3Client.deleteObject(deleteRequest)
        log.info { "Deleted R2 object: key=$storedKey" }
    }

    fun getObjectHeadBytes(storedKey: String, numBytes: Int): ByteArray {
        val getRequest = GetObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(storedKey)
            .range("bytes=0-${numBytes - 1}")
            .build()

        val response = s3Client.getObjectAsBytes(getRequest)
        return response.asByteArray()
    }

    fun getObjectBytes(storedKey: String): ByteArray {
        val getRequest = GetObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(storedKey)
            .build()

        val response = s3Client.getObjectAsBytes(getRequest)
        return response.asByteArray()
    }

    fun putObject(storedKey: String, bytes: ByteArray, contentType: String) {
        val putRequest = PutObjectRequest.builder()
            .bucket(commonR2Properties.bucketName)
            .key(storedKey)
            .contentType(contentType)
            .contentLength(bytes.size.toLong())
            .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(bytes))
        log.info { "Uploaded R2 object: key=$storedKey, size=${bytes.size}" }
    }

    fun copyObject(sourceKey: String, destinationKey: String) {
        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(commonR2Properties.bucketName)
            .sourceKey(sourceKey)
            .destinationBucket(commonR2Properties.bucketName)
            .destinationKey(destinationKey)
            .build()

        s3Client.copyObject(copyRequest)
        log.info { "Copied R2 object: sourceKey=$sourceKey, destinationKey=$destinationKey" }
    }
}
