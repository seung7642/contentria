package com.contentria.worker.storage

import com.contentria.common.global.config.R2Properties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File

private val log = KotlinLogging.logger {}

/** R2 access for the worker: download the raw source, upload the HLS outputs. */
@Component
class WorkerStorageClient(
    private val s3Client: S3Client,
    private val r2Properties: R2Properties,
) {

    fun download(key: String, destination: File) {
        s3Client.getObject(
            GetObjectRequest.builder().bucket(r2Properties.bucketName).key(key).build(),
            destination.toPath(),
        )
        log.debug { "Downloaded R2 object key=$key -> ${destination.absolutePath} (${destination.length()} bytes)" }
    }

    fun upload(key: String, file: File, contentType: String) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(r2Properties.bucketName)
                .key(key)
                .contentType(contentType)
                .build(),
            RequestBody.fromFile(file),
        )
        log.debug { "Uploaded R2 object key=$key (${file.length()} bytes, $contentType)" }
    }

    fun delete(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(r2Properties.bucketName).key(key).build()
        )
        log.debug { "Deleted R2 object key=$key" }
    }
}
