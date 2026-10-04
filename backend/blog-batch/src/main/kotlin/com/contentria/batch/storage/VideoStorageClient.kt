package com.contentria.batch.storage

import com.contentria.common.global.config.R2Properties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.ObjectIdentifier

private val log = KotlinLogging.logger {}

/** R2 access for video GC: delete a single object and recursively delete a key prefix. */
@Component
class VideoStorageClient(
    private val s3Client: S3Client,
    private val r2Properties: R2Properties,
) {

    fun delete(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(r2Properties.bucketName).key(key).build()
        )
        log.debug { "Deleted R2 object key=$key" }
    }

    /** Lists and deletes every object under [prefix] (e.g. `hls/{id}/`), paging through results. */
    fun deletePrefix(prefix: String) {
        var continuationToken: String? = null
        var deleted = 0
        do {
            val listResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                    .bucket(r2Properties.bucketName)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build()
            )
            val keys = listResponse.contents().map { it.key() }
            if (keys.isNotEmpty()) {
                s3Client.deleteObjects(
                    DeleteObjectsRequest.builder()
                        .bucket(r2Properties.bucketName)
                        .delete(
                            Delete.builder()
                                .objects(keys.map { ObjectIdentifier.builder().key(it).build() })
                                .build()
                        )
                        .build()
                )
                deleted += keys.size
            }
            continuationToken = if (listResponse.isTruncated) listResponse.nextContinuationToken() else null
        } while (continuationToken != null)

        log.debug { "Deleted $deleted R2 object(s) under prefix=$prefix" }
    }
}
