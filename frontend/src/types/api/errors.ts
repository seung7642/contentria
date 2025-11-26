export class ApiError extends Error {
  public readonly timestamp: string;
  public readonly status: number;
  public readonly error: string; // e.g., "Bad Request"
  public readonly code: string; // e.g., "AU0001"
  public readonly path?: string;
  public readonly details?: Record<string, string>;

  constructor(
    message: string,
    timestamp: string,
    status: number,
    error: string,
    code: string,
    path?: string,
    details?: Record<string, string>
  ) {
    super(message);
    this.name = 'ApiError';
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.code = code;
    this.path = path;
    this.details = details;
  }
}

// 백엔드에서 반환하는 표준 에러 응답의 타입 정의
// 백엔드에서 반환되는 스펙은 접미사로 'Response'를 붙인다.
export interface ApiErrorResponse {
  message: string;
  timestamp: string;
  status: number;
  error: string;
  code: string;
  path: string;
  details?: Record<string, string>;
}
