import { PATHS } from '@/constants/paths';
import { ApiError, ApiErrorResponse } from '@/types/api/errors';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

// AxiosResponse와 구조를 맞추기 위한 인터페이스
interface ServerResponse<T> {
  data: T;
}

// 공통 Fetch 로직 분리
const fetchExtended = async <T>(
  path: string,
  options: RequestInit = {}
): Promise<ServerResponse<T>> => {
  const endpoint = `${API_BASE_URL}${path}`;
  const cookieStore = await cookies();
  const accessToken = cookieStore.get('accessToken')?.value;

  const headers = new Headers(options.headers);
  headers.set('Content-Type', 'application/json');

  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  try {
    const response = await fetch(endpoint, {
      cache: 'no-store',
      ...options,
      headers,
    });

    // 1. 에러 처리 (reject)
    if (!response.ok) {
      // 404 등에서 body가 비어있을 수 있으므로 안전하게 파싱
      const errorData = (await response.json().catch(() => ({}))) as ApiErrorResponse;

      if (response.status === 401) {
        redirect(PATHS.LOGIN);
      }

      const apiError = new ApiError(
        errorData.message || 'Server Error',
        errorData.timestamp,
        errorData.status || response.status,
        errorData.error,
        errorData.code,
        errorData.path,
        errorData.details
      );

      throw apiError; // 여기서 값을 리턴하지 않고 던져야 try-catch에 걸립니다.
    }

    // 2. 성공 처리 (resolve)
    // Axios 처럼 { data: ... } 형태로 감싸서 반환
    const data = (await response.json()) as T;
    return { data };
  } catch (error) {
    // Next.js의 redirect()는 내부적으로 에러를 throw하여 작동하므로 다시 던져줘야 함
    if (isNextRedirectError(error)) {
      throw error;
    }

    // 이미 우리가 던진 ApiError라면 그대로 통과
    if (error instanceof ApiError) {
      throw error;
    }

    // 그 외(네트워크 에러 등)는 일반 Error로 변환하여 던짐 (AxiosError 대체)
    throw new Error(`Network or Server Error: ${(error as Error).message}`);
  }
};

// Next.js Redirect Error 판별 헬퍼
function isNextRedirectError(error: any): boolean {
  return (
    typeof error === 'object' &&
    error !== null &&
    'digest' in error &&
    typeof (error as any).digest === 'string' &&
    (error as any).digest.startsWith('NEXT_REDIRECT')
  );
}

const apiServer01 = {
  get: <T>(path: string, options?: RequestInit) =>
    fetchExtended<T>(path, { method: 'GET', ...options }),

  // body 타입을 string이 아닌 any나 unknown으로 받아서 내부에서 stringify 하는 게 apiClient와 더 비슷합니다.
  post: <T>(path: string, body: unknown, options?: RequestInit) =>
    fetchExtended<T>(path, { method: 'POST', body: JSON.stringify(body), ...options }),

  put: <T>(path: string, body: unknown, options?: RequestInit) =>
    fetchExtended<T>(path, { method: 'PUT', body: JSON.stringify(body), ...options }),

  delete: <T>(path: string, options?: RequestInit) =>
    fetchExtended<T>(path, { method: 'DELETE', ...options }),
};

export default apiServer01;
