import imageCompression from 'browser-image-compression';
import { requestPresignedUrlAction } from '@/actions/media';

const COMPRESSION_OPTIONS = {
  maxSizeMB: 1,
  maxWidthOrHeight: 1920,
  initialQuality: 0.8,
  useWebWorker: true,
};

const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

/**
 * Compresses an image, requests a presigned URL, uploads to R2,
 * and returns the public CDN URL.
 */
export async function uploadImageToR2(file: File): Promise<string> {
  if (!ALLOWED_TYPES.includes(file.type)) {
    throw new Error('Unsupported file type. Allowed: JPEG, PNG, WebP, GIF.');
  }

  if (file.size > MAX_FILE_SIZE) {
    throw new Error('File size exceeds 10MB limit.');
  }

  // Compress the image (skip GIFs to preserve animation)
  const compressedFile = file.type === 'image/gif' ? file : await imageCompression(file, COMPRESSION_OPTIONS);

  // Request presigned URL from backend via Server Action
  const { presignedUrl, publicUrl } = await requestPresignedUrlAction({
    fileName: file.name,
    contentType: compressedFile.type,
    fileSize: compressedFile.size,
  });

  // Upload directly to R2
  const uploadResponse = await fetch(presignedUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': compressedFile.type,
      'Content-Length': compressedFile.size.toString(),
    },
    body: compressedFile,
  });

  if (!uploadResponse.ok) {
    throw new Error(`Upload to R2 failed: ${uploadResponse.status}`);
  }

  return publicUrl;
}
