import { REFRESH_URL } from '@/constants/auth';
import { useAuthStore } from '@/store/authStore';
import { ApiError, ApiErrorResponse } from '@/types/api/errors';

let isRefreshing = false;
let refreshPromise: Promise<void> | null = null;

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

const makeRequest = async (url: string, options: RequestInit): Promise<Response> => {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
  const fetchOptions: RequestInit = {
    ...options,
    credentials: 'include', // Include cookies in the request
  };
  return await fetch(fullUrl, fetchOptions);
};

const handleTokenRefresh = async (): Promise<void> => {
  console.log('apiClient handleTokenRefresh start');

  isRefreshing = true;
  const refreshResponse = await makeRequest(REFRESH_URL, { method: 'POST' });

  if (!refreshResponse.ok) {
    const errorData: ApiErrorResponse = await refreshResponse.json();
    throw new ApiError(
      `Token refresh failed with status ${refreshResponse.status}: ${errorData.message}`,
      refreshResponse.status,
      errorData
    );
  }

  isRefreshing = false;
  refreshPromise = null;
};

const parseResponse = async <T>(response: Response): Promise<T> => {
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json() as Promise<T>;
  } else if (response.status === 204 || response.headers.get('content-length') === '0') {
    // No Content response
    return null as unknown as Promise<T>;
  } else {
    // Handle non-JSON responses
    return response.text() as unknown as Promise<T>;
  }
};

const handle401Error = async (url: string, options: RequestInit): Promise<Response> => {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshPromise = handleTokenRefresh();
  }
  await refreshPromise;
  return await makeRequest(url, options);
};

async function apiClient<T>(url: string, options: RequestInit = {}, retryCount = 1): Promise<T> {
  try {
    let response = await makeRequest(url, options);

    if (response.status === 401) {
      response = await handle401Error(url, options);
      if (response.status === 401 && retryCount > 0) {
        return apiClient(url, options, retryCount - 1);
      }
      if (response.status === 401) {
        useAuthStore.getState().setUser(null);
        throw new ApiError('Authentication failed even after token refresh.', 401);
      }
    }

    // Handle non-401 errors after potential retry
    if (!response.ok) {
      const errorData: ApiErrorResponse = await response.json();
      throw new ApiError(`API Error: ${response.statusText}`, response.status, errorData);
    }

    return parseResponse<T>(response);
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    if (error instanceof Error) {
      throw new ApiError('Unexpected error occurred', 500, error.message);
    }
    throw new ApiError('Unknown error occurred in apiClient', 500, String(error));
  }
}

export default apiClient;
