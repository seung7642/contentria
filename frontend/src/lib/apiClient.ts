import { REFRESH_URL } from '@/constants/auth';
import { useAuthStore } from '@/store/authStore';
import {
  ApiError,
  ApiErrorDetailType,
  ApiErrorResponse,
  AuthError,
  GenericErrorDetails,
  UnknownErrorDetails,
} from '@/types/api/errors';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';
let isRefreshing = false;
let refreshPromise: Promise<void> | null = null;

const makeRequest = async (url: string, options: RequestInit): Promise<Response> => {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
  const fetchOptions: RequestInit = {
    ...options,
    credentials: 'include', // Include cookies in the request
  };
  try {
    return await fetch(fullUrl, fetchOptions);
  } catch (error: unknown) {
    const detail: GenericErrorDetails = {
      _detailType: 'GenericError',
      name: (error as Error).name || 'NetworkError',
      message: (error as Error).message || 'Network request failed',
      stack: (error as Error).stack,
    };
    throw new ApiError((error as Error).message || 'Network request failed', 0, detail);
  }
};

const parseErrorResponse = async (
  response: Response
): Promise<ApiErrorResponse | { message: string }> => {
  try {
    return await response.json();
  } catch (error: unknown) {
    const message = (error as Error).message || String(error);
    return {
      message: `Failed to parse error response (status: ${response.status}, statusText: ${response.statusText}). Original error: ${message}`,
    };
  }
};

const performTokenRefresh = async (): Promise<void> => {
  const response = await makeRequest(REFRESH_URL, { method: 'POST' });

  if (!response.ok) {
    const errorData = await parseErrorResponse(response);

    let detailForError: ApiErrorDetailType;
    if ('timestamp' in errorData && 'path' in errorData) {
      detailForError = errorData as ApiErrorResponse;
    } else {
      const fallbackMessage = (errorData as { message: string }).message;
      detailForError = {
        _detailType: 'GenericError',
        name: 'ErrorResponseParseFailure',
        message: fallbackMessage,
        stack: undefined,
      };
    }

    const errorMessage =
      'message' in errorData && errorData.message
        ? errorData.message
        : `Token refresh failed: ${response.status}`;

    throw new AuthError(errorMessage!, response.status, detailForError);
  }
};

const ensureTokenRefreshedAndClearState = async (): Promise<void> => {
  try {
    // 토큰 갱신 작업의 생명주기와 상태 변수의 생명주기를 정확히 일치시켜 동시성 문제를 해결한다.
    await performTokenRefresh();
  } finally {
    isRefreshing = false;
    refreshPromise = null;
  }
};

const handle401Error = async (url: string, options: RequestInit): Promise<Response> => {
  try {
    // 여러 API 요청이 동시에 401 에러를 받을 경우, 토큰 갱신을 한 번만 수행하도록 한다.
    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = ensureTokenRefreshedAndClearState();
    }
    await refreshPromise;
    return await makeRequest(url, options);
  } catch (error: unknown) {
    if (error instanceof AuthError) {
      useAuthStore.getState().setUser(null);
    }
    throw error;
  }
};

const parseResponse = async <T>(response: Response): Promise<T> => {
  const contentType = response.headers.get('content-type');
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return null as unknown as T;
  }
  if (contentType && contentType.includes('application/json')) {
    return response.json() as Promise<T>;
  }
  return response.text() as unknown as Promise<T>;
};

const apiClient = async <T>(url: string, options: RequestInit = {}): Promise<T> => {
  try {
    let response = await makeRequest(url, options);

    if (response.status === 401) {
      response = await handle401Error(url, options);
      if (response.status === 401) {
        useAuthStore.getState().setUser(null);
        const errorData = await parseErrorResponse(response);

        let detailForAuthError: ApiErrorDetailType;
        if ('timestamp' in errorData && 'path' in errorData) {
          detailForAuthError = errorData as ApiErrorResponse;
        } else {
          detailForAuthError = {
            _detailType: 'GenericError',
            name: 'PostRefreshAuthFailure',
            message: (errorData as { message: string }).message,
            stack: undefined,
          };
        }

        const errorMessage =
          'message' in errorData && errorData.message
            ? errorData.message
            : 'Authentication failed even after token refresh.';

        throw new AuthError(errorMessage!, 401, detailForAuthError);
      }
    }

    // Handle non-401 errors after potential retry
    if (!response.ok) {
      const errorData = await parseErrorResponse(response);
      throw new ApiError(
        (errorData as ApiErrorResponse).message ||
          (errorData as { message: string }).message ||
          `API Error: ${response.statusText}`,
        response.status,
        errorData as ApiErrorResponse
      );
    }

    return await parseResponse<T>(response);
  } catch (error: unknown) {
    if (error instanceof ApiError || error instanceof AuthError) {
      throw error;
    }
    if (error instanceof Error) {
      throw new ApiError(
        error.message || 'A client-side or network error occurred in apiClient.',
        0,
        {
          _detailType: 'GenericError',
          name: error.name,
          message: error.message,
          stack: error.stack,
        } as GenericErrorDetails
      );
    }
    throw new ApiError('An unknown error occurred in apiClient', 0, {
      _detailType: 'UnknownError',
      thrownValue: String(error),
    } as UnknownErrorDetails);
  }
};

export default apiClient;
