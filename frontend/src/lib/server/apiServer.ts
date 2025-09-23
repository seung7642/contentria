import { PATHS } from '@/constants/paths';
import { ApiError, BackendErrorResponse } from '@/errors/ApiError';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

interface FetchOptions extends RequestInit {
  next?: {
    revalidate?: number | false;
    tags?: string[];
  };
}

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

const apiServer = {
  async get<T>(path: string, options?: FetchOptions): Promise<T | null> {
    const endpoint = `${API_BASE_URL}${path}`;
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('accessToken')?.value;

    const headers = new Headers(options?.headers);
    headers.set('Content-Type', 'application/json');
    if (accessToken) {
      headers.set('Authorization', `Bearer ${accessToken}`);
    }

    try {
      const response = await fetch(endpoint, {
        method: 'GET',
        ...options,
        headers,
      });

      if (!response.ok) {
        const errorData: BackendErrorResponse = await response.json();
        const apiError = new ApiError(
          errorData.message,
          errorData.timestamp,
          errorData.status,
          errorData.error,
          errorData.code,
          errorData.path,
          errorData.details
        );

        switch (apiError.status) {
          case 401:
            return redirect(PATHS.LOGIN);
          case 404:
            return null; // 리소스 없음 -> 페이지에서 notFound() 호출하도록 null 반환
          default:
            throw apiError; // 그 외 에러는 error.tsx 바운더리가 처리하도록 에러를 던짐
        }
      }

      return response.json() as T;
    } catch (error) {
      if (
        typeof error === 'object' &&
        error !== null &&
        'digest' in error &&
        typeof (error as { digest?: string }).digest === 'string' &&
        (error as { digest: string }).digest?.startsWith('NEXT_REDIRECT')
      ) {
        throw error;
      }
      console.error(`[apiServer GET Error] for path ${path}:`, error);
      return null;
    }
  },
};

export default apiServer;
