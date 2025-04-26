import { REFRESH_URL } from '@/constants/auth';
import { useAuthStore } from '@/store/authStore';

let isRefreshing = false;
let refreshPromise: Promise<void> | null = null;

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

const handleTokenRefresh = async (): Promise<void> => {
  console.log('apiClient handleTokenRefresh start');

  isRefreshing = true;
  try {
    const refreshResponse = await fetch(`${API_BASE_URL}${REFRESH_URL}`, {
      method: 'POST',
    });

    if (!refreshResponse.ok) {
      const errorData = await refreshResponse.json();
      const errorDetails = JSON.stringify(errorData);

      console.error(
        `apiClient: Token refresh request failed with status ${refreshResponse.status}. Details: ${errorDetails}`
      );
      useAuthStore.getState().setUser(null);
      throw new Error(
        `Token refresh failed with status ${refreshResponse.status}. User has been logged out.`
      );
    }

    console.log('apiClient: Token refresh successful.');
  } catch (error) {
    console.error('apiClient: Error during token refresh process:', error);
    useAuthStore.getState().setUser(null);
    throw error;
  } finally {
    isRefreshing = false;
    refreshPromise = null;
    console.log('apiClient handleTokenRefresh end');
  }
};

const makeRequest = async (url: string, options: RequestInit): Promise<Response> => {
  try {
    const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
    return await fetch(fullUrl, options);
  } catch (error) {
    console.error(`apiClient: Network or fetch error for ${url}:`, error);
    throw new Error('Network request failed');
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
  // For Client-side fetch, the browser automatically attaches cookies (including HttpOnly)
  // So, we don't need to explicitly set the Authorization header by default.

  let response = await makeRequest(url, options);

  if (response.status === 401) {
    console.log(`apiClient: Received 401 for ${url}.`);

    if (!isRefreshing) {
      isRefreshing = true;
      refreshPromise = handleTokenRefresh();
    }

    try {
      await refreshPromise;
      console.log(`apiClient: Retrying original request for ${url} after refresh.`);
      response = await makeRequest(url, options);

      if (response.status === 401) {
        console.error(
          `apiClient: Still received 401 for ${url} after refresh attempt. Logging out.`
        );
        useAuthStore.getState().setUser(null);
        throw new Error('Authentication failed even after token refresh.');
      }
    } catch (refreshError) {
      console.error(`apiClient: Not retrying ${url} due to refresh failure.`);
      throw refreshError;
    }
  }

  // Handle non-401 errors after potential retry
  if (!response.ok) {
    const errorBody = await response.text();
    console.error(
      `apiClient: API request failed with status ${response.status} for ${url}. Body: ${errorBody}`
    );
    throw new Error(`API Error: ${response.status} ${response.statusText}`);
  }

  return parseResponse<T>(response, url);
}

export default apiClient;
