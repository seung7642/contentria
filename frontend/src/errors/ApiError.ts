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

export interface BackendErrorResponse {
  message: string;
  timestamp: string;
  status: number;
  error: string;
  code: string;
  path: string;
  details?: Record<string, string>;
}
