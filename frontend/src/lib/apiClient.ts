import { REFRESH_URL } from '@/constants/auth';
import { useAuthStore } from '@/store/authStore';
import { ApiErrorResponse } from '@/types/api';

let isRefreshing = false;
let refreshPromise: Promise<void> | null = null;

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

const createApiError = (
  message: string,
  status?: number,
  details?: ApiErrorResponse | string
): Error & { status?: number; details?: ApiErrorResponse | string } => {
  const error = new Error(message) as Error & {
    status?: number;
    details?: ApiErrorResponse | string;
  };
  if (status) {
    error.status = status;
  }
  if (details) {
    error.details = details;
  }
  return error;
};

const makeRequest = async (url: string, options: RequestInit): Promise<Response> => {
  try {
    const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
    const fetchOptions: RequestInit = {
      ...options,
      credentials: 'include', // Include cookies in the request
    };
    return await fetch(fullUrl, fetchOptions);
  } catch (error) {
    console.error(`apiClient: Network or fetch error for ${url}:`, error);
    throw new Error('Network request failed');
  }
};

const handleTokenRefresh = async (): Promise<void> => {
  console.log('apiClient handleTokenRefresh start');

  isRefreshing = true;
  try {
    const refreshResponse = await makeRequest(REFRESH_URL, { method: 'POST' });

    if (!refreshResponse.ok) {
      const errorData = await refreshResponse.json();
      const errorDetails = JSON.stringify(errorData);
      useAuthStore.getState().setUser(null);
      throw createApiError(
        `Token refresh failed. User logged out.`,
        refreshResponse.status,
        errorDetails
      );
    }

    console.log('apiClient: Token refresh successful.');
  } catch (error) {
    console.debug('apiClient: Error during token refresh process:', error);
    useAuthStore.getState().setUser(null);
    throw error;
  } finally {
    isRefreshing = false;
    refreshPromise = null;
    console.log('apiClient handleTokenRefresh end');
  }
};

const parseResponse = async <T>(response: Response, url: string): Promise<T> => {
  try {
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
  } catch (parseError) {
    console.error(`apiClient: Error parsing response for ${url}:`, parseError);
    throw new Error('Failed to parse API response.');
  }
};

async function apiClient<T>(url: string, options: RequestInit = {}): Promise<T> {
  let response = await makeRequest(url, options);

  if (response.status === 401) {
    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = handleTokenRefresh();
    }

    try {
      await refreshPromise;
      console.log(`apiClient: Retrying original request for ${url} after refresh.`);
      response = await makeRequest(url, options);

      if (response.status === 401) {
        console.log(`apiClient: Still received 401 for ${url} after refresh attempt. Logging out.`);
        useAuthStore.getState().setUser(null);
        throw createApiError('Authentication failed even after token refresh.', 401);
      }
    } catch (refreshError) {
      console.debug(`apiClient: Not retrying ${url} due to refresh failure.`);
      throw refreshError;
    }
  }

  // Handle non-401 errors after potential retry
  if (!response.ok) {
    const errorData = await response.json();
    const errorDetails = JSON.stringify(errorData);

    console.log(
      `apiClient: API request failed with status ${response.status} for ${url}. Details:`,
      errorDetails
    );
    throw createApiError(`API Error: ${response.statusText}`, response.status, errorDetails);
  }

  return parseResponse<T>(response, url);
}

export default apiClient;
