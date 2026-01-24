import { PATHS } from '@/constants/paths';
import { ApiError, ApiErrorResponse } from '@/types/api/errors';
import { isRedirectError } from 'next/dist/client/components/redirect-error';
import { ReadonlyRequestCookies } from 'next/dist/server/web/spec-extension/adapters/request-cookies';
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
    try {
      const newAccessToken = await refreshTokens(cookieStore, refreshToken, shouldRedirectOn401);
      if (newAccessToken) {
        headers.set('Authorization', `Bearer ${newAccessToken}`);
        response = await fetch(`${API_BASE_URL}${url}`, { ...rest, headers });
        console.log('✅ [apiServer] Retried request after token refresh.');
      }
    } catch (error) {
      if (isRedirectError(error)) {
        throw error;
      }
      console.warn('❌ [apiServer] Token refresh failed:', error);
    }
  }

  if (response.status === 401 && requireAuth && shouldRedirectOn401) {
    console.log('🔒 [apiServer] Unauthorized. Redirecting to login.');
    redirect(`${PATHS.LOGIN}?alert=session_expired`);
  }

  if (!response.ok) {
    const errorData: Partial<ApiErrorResponse> = await response.json().catch(() => ({}));
    console.log('❌ [apiServer] API error response:', errorData);
    throw ApiError.from(errorData, response.status);
  }

  return response.json();
}

/**
 * Pure function responsible only for token refresh and cookie setting
 * @returns newAccessToken (newAccessToken upon successful refresh)
 */
async function refreshTokens(
  cookieStore: ReadonlyRequestCookies,
  refreshToken: string,
  shouldRedirectOn401: boolean
): Promise<string> {
  console.log('🔄 [apiServer] Attempting to refresh tokens.');

  const refreshResponse = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Cookie: `refreshToken=${refreshToken}`,
    },
  });

  if (!refreshResponse.ok) {
    console.error('❌ [apiServer] Refresh failed. Logging out');

    try {
      cookieStore.delete('accessToken');
      cookieStore.delete('refreshToken');
    } catch (error) {
      // apiServer는 오직 서버 액션을 통해서만 호출된다.
      // 클라이언트 컴포넌트에서 서버 액션이 호출될 때는 HTTP 요청이 발생하기 때문에 쿠키 설정이 가능하지만,
      // 서버 컴포넌트에서 서버 액션이 호출될 때는 단순 함수 호출로 처리되기 때문에 쿠키 설정이 불가능하다.
      console.error('❌ [apiServer] Error deleting cookies during logout:', error);
    }

    if (shouldRedirectOn401) {
      redirect(`${PATHS.LOGIN}?alert=session_expired`);
    } else {
      throw new Error('Unauthorized');
    }
  }

  const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
    await refreshResponse.json();

  try {
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
  } catch (error) {
    // apiServer는 오직 서버 액션을 통해서만 호출된다.
    // 클라이언트 컴포넌트에서 서버 액션이 호출될 때는 HTTP 요청이 발생하기 때문에 쿠키 설정이 가능하지만,
    // 서버 컴포넌트에서 서버 액션이 호출될 때는 단순 함수 호출로 처리되기 때문에 쿠키 설정이 불가능하다.
    console.error('❌ [apiServer] Error setting cookies during token refresh:', error);
  }

  return newAccessToken;
}

export default apiServer;
