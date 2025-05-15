export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string; // HTTP 상태 코드에 대응하는 간단한 설명을 제공한다. (예: "Bad Request", "Unauthorized")
  message: string | null;
  path: string | null; // 요청한 URL 경로를 포함한다.
}

export class ApiError extends Error {
  status: number;
  details?: ApiErrorResponse | string;

  constructor(message: string, status: number, details?: ApiErrorResponse | string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

export class AuthError extends Error {
  status: number;
  details?: ApiErrorResponse | string;

  constructor(message: string, status: number, details?: ApiErrorResponse | string) {
    super(message);
    this.name = 'AuthError';
    this.status = status;
    this.details = details;
  }
}
