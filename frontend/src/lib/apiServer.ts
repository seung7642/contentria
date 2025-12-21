import { PATHS } from '@/constants/paths';
import { ApiError, ApiErrorResponse } from '@/types/api/errors';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

type FetchOptions = RequestInit & {
  requireAuth?: boolean; // 인증 필요 여부 (기본값: true)
  shouldRedirectOn401?: boolean;
};

export const apiServer = {
  get: <T>(url: string, options?: FetchOptions) =>
    fetchExtended<T>(url, { ...options, method: 'GET' }),

  post: <T>(url: string, body: any, options?: FetchOptions) =>
    fetchExtended<T>(url, { ...options, method: 'POST', body: JSON.stringify(body) }),

  put: <T>(url: string, body: any, options?: FetchOptions) =>
    fetchExtended<T>(url, { ...options, method: 'PUT', body: JSON.stringify(body) }),

  delete: <T>(url: string, options?: FetchOptions) =>
    fetchExtended<T>(url, { ...options, method: 'DELETE' }),
};

async function fetchExtended<T>(url: string, options: FetchOptions = {}): Promise<T> {
  const {
    requireAuth = true,
    shouldRedirectOn401 = true,
    headers: customHeaders,
    ...rest
  } = options;
  const cookieStore = await cookies();
  const accessToken = cookieStore.get('accessToken')?.value;
  const refreshToken = cookieStore.get('refreshToken')?.value;
  console.log(
    `[apiServer] accessToken: ${accessToken?.substring(0, 10) ?? ''}, refreshToken: ${refreshToken?.substring(0, 10) ?? ''}`
  );

  const headers = new Headers(customHeaders);
  headers.set('Content-Type', 'application/json');

  if (requireAuth && accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  let response = await fetch(`${API_BASE_URL}${url}`, { ...rest, headers });

  if (response.status === 401 && requireAuth && refreshToken) {
    console.log('🔄 [apiServer] accessToken expired, attempt to refresh');

    const refreshResponse = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Cookie: `refreshToken=${refreshToken}`,
      },
    });

    if (!refreshResponse.ok) {
      console.error('❌ [apiServer] refreshToken expired. Logging out');

      cookieStore.delete('accessToken');
      cookieStore.delete('refreshToken');

      if (shouldRedirectOn401) {
        redirect(PATHS.LOGIN);
      } else {
        throw new Error('Unauthorized');
      }
    }

    const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
      await refreshResponse.json();

    cookieStore.set('accessToken', newAccessToken, {
      httpOnly: true,
      path: '/',
      maxAge: 15 * 60, // 15 minutes
    });

    if (newRefreshToken) {
      cookieStore.set('refreshToken', newRefreshToken, {
        httpOnly: true,
        path: '/',
        maxAge: 7 * 24 * 60 * 60, // 7 days
      });
    }

    headers.set('Authorization', `Bearer ${newAccessToken}`);
    response = await fetch(`${API_BASE_URL}${url}`, { ...rest, headers });

    console.log('✅ [apiServer] accessToken refreshed and request retried successfully');
  }

  if (!response.ok) {
    const errorData: ApiErrorResponse = await response.json().catch(() => ({}));
    throw new ApiError(
      errorData.message || 'Server Error',
      errorData.timestamp,
      errorData.status || response.status,
      errorData.error,
      errorData.code,
      errorData.path,
      errorData.details
    );
  }

  return response.json();
}

export default apiServer;
