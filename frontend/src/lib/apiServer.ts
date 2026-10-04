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

  post: <T>(url: string, body: unknown, options?: FetchOptions) =>
    fetchExtended<T>(url, { ...options, method: 'POST', body: JSON.stringify(body) }),

  put: <T>(url: string, body: unknown, options?: FetchOptions) =>
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

  console.log(`[apiServer] accessToken: ${accessToken?.substring(0, 10) ?? 'none'}`);

  const headers = new Headers(customHeaders);
  headers.set('Content-Type', 'application/json');

  if (requireAuth && accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  const response = await fetch(`${API_BASE_URL}${url}`, { ...rest, headers });

  if (response.status === 401 && requireAuth && shouldRedirectOn401) {
    console.log('🔒 [apiServer] Unauthorized. Redirecting to login.');
    redirect(`${PATHS.LOGIN}?alert=session_expired`); // Throw a `NEXT_REDIRECT` error catched by Next.js framework.
  }

  if (!response.ok) {
    const errorData: Partial<ApiErrorResponse> = await response.json().catch(() => ({}));
    const isExpectedAuthCheck = response.status === 401 && !shouldRedirectOn401;
    if (!isExpectedAuthCheck) {
      console.log(`❌ [apiServer] API error response (Status: ${response.status}):`, errorData);
    }

    throw ApiError.from(errorData, response.status);
  }

  const text = await response.text();
  if (!text) {
    return undefined as T;
  }
  return JSON.parse(text);
}

export default apiServer;
