export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  code: string;
}

export class ApiError extends Error {
  public readonly status: number;
  public readonly code: string;
  public readonly details?: ErrorResponse;

  constructor(errorResponse: ErrorResponse) {
    super(errorResponse.message);
    this.name = 'ApiError';
    this.status = errorResponse.status;
    this.code = errorResponse.code;
    this.details = errorResponse;
  }
}
