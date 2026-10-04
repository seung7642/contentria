package com.contentria.worker.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "transcode")
data class TranscodeProperties(
    val ffmpegPath: String = "ffmpeg",
    val ffprobePath: String = "ffprobe",
    // Permanent-reject inputs longer than this (matches the presigned-side product cap).
    val maxDurationSeconds: Long = 300,
    val hlsSegmentSeconds: Int = 6,
    // Hard timeout for a single ffmpeg/ffprobe process (> worst-case transcode).
    val processTimeoutMinutes: Long = 20,
    // Scratch dir for downloads + HLS output; null → JVM temp dir (mount an emptyDir here in K8s).
    val workDir: String? = null,
)
