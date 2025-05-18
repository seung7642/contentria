export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string; // HTTP 상태 코드에 대응하는 간단한 설명을 제공한다. (예: "Bad Request", "Unauthorized")
  message: string | null;
  path: string | null; // 요청한 URL 경로를 포함한다.
}

export interface GenericErrorDetails {
  _detailType: 'GenericError';
  name: string;
  message: string;
  stack?: string;
}

export interface UnknownErrorDetails {
  _detailType: 'UnknownError';
  thrownValue: string;
}

export type ApiErrorDetailType =
  | ApiErrorResponse
  | GenericErrorDetails
  | UnknownErrorDetails
  | string;

export class ApiError extends Error {
  status: number;
  details?: ApiErrorDetailType;

  constructor(message: string, status: number, details?: ApiErrorDetailType) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

export class AuthError extends ApiError {
  constructor(message: string, status: number, details?: ApiErrorDetailType) {
    super(message, status, details);
    this.name = 'AuthError';
  }
}
