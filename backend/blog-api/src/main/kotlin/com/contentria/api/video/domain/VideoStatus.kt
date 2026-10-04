package com.contentria.api.video.domain

enum class VideoStatus {
    PENDING,      // uploaded, waiting to be transcoded
    PROCESSING,   // claimed by a worker, transcoding in progress
    COMPLETED,    // transcoded HLS outputs available
    FAILED,       // transcoding failed after max attempts
    DELETED;      // marked for cleanup (replaced/removed), GC will hard-delete

    fun isCompleted(): Boolean {
        return this == COMPLETED
    }
}
