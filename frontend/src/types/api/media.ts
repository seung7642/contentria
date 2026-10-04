export interface PresignedUrlRequest {
  fileName: string;
  contentType: string;
  fileSize: number;
}

export interface PresignedUrlResponse {
  presignedUrl: string;
  publicUrl: string;
  mediaId: string;
}
