package com.contentria.api.media.application

import org.springframework.stereotype.Component

@Component
class MediaValidator {

    fun validateMagicNumber(headerBytes: ByteArray, declaredContentType: String): Boolean {
        val signatures = MAGIC_NUMBERS[declaredContentType] ?: return false
        return signatures.any { signature -> headerBytes.startsWith(signature) }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        return prefix.indices.all { this[it] == prefix[it] }
    }

    companion object {
        // JPEG: FF D8 FF
        private val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        private val PNG_SIGNATURE = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        )

        // GIF87a / GIF89a: 47 49 46 38
        private val GIF_SIGNATURE = byteArrayOf(0x47, 0x49, 0x46, 0x38)

        // WebP: RIFF....WEBP — bytes 0-3 are "RIFF", bytes 8-11 are "WEBP"
        private val WEBP_RIFF_PREFIX = byteArrayOf(0x52, 0x49, 0x46, 0x46)
        private val WEBP_MARKER = byteArrayOf(0x57, 0x45, 0x42, 0x50)

        val MAGIC_NUMBERS: Map<String, List<ByteArray>> = mapOf(
            "image/jpeg" to listOf(JPEG_SIGNATURE),
            "image/png" to listOf(PNG_SIGNATURE),
            "image/gif" to listOf(GIF_SIGNATURE),
            "image/webp" to listOf(WEBP_RIFF_PREFIX)  // initial check; WebP also verified at offset 8
        )

        const val HEADER_BYTES_NEEDED = 12

        fun isValidWebP(headerBytes: ByteArray): Boolean {
            if (headerBytes.size < 12) return false
            val hasRiff = WEBP_RIFF_PREFIX.indices.all { headerBytes[it] == WEBP_RIFF_PREFIX[it] }
            val hasWebp = WEBP_MARKER.indices.all { headerBytes[it + 8] == WEBP_MARKER[it] }
            return hasRiff && hasWebp
        }
    }
}
