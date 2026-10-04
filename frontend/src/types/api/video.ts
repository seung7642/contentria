export interface VideoPresignedUrlRequest {
  fileName: string;
  contentType: string;
  fileSize: number;
}

export interface VideoPresignedUrlResponse {
  presignedUrl: string;
  videoId: string;
}

export type VideoStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'DELETED';
