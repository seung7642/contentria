import { PATHS } from '@/constants/paths';
import { ApiError, BackendErrorResponse } from '@/types/api/errors';
import { useAuthStore } from '@/store/authStore';
import axios, { AxiosError } from 'axios';
import createAuthRefreshInterceptor from 'axios-auth-refresh';
import { getCookie } from 'cookies-next';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true, // HttpOnly 쿠키(refreshToken)를 주고받기 위한 설정
});

// 1. 요청 인터셉터: 모든 요청에 액세스 토큰을 자동으로 추가한다.
apiClient.interceptors.request.use(
  (config) => {
    const accessToken = getCookie('accessToken');
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 2. 응답 인터셉터: 백엔드에서 에러 응답을 보낼 때 에러 객체를 표준화한다.
apiClient.interceptors.response.use(
  (response) => response, // 2xx 응답은 그대로 통과
  (error: AxiosError) => {
    if (error.response && isBackendErrorResponse(error.response.data)) {
      // error.response.data는 자동으로 'BackendErrorResponse' 타입으로 추론된다.
      // 'as BackendErrorResponse' 같은 강제 형변환이 필요없다.
      const errorData = error.response.data;
      return Promise.reject(
        new ApiError(
          errorData.message,
          errorData.timestamp,
          errorData.status,
          errorData.error,
          errorData.code,
          errorData.path,
          errorData.details
        )
      );
    }

    // 네트워크 에러 등 백엔드 응답이 없는 경우, 기본 에러(AxiosError)를 그대로 reject
    return Promise.reject(error);
  }
);

// 타입스크립트 문법: User-Defined Type Guard
//   함수가 true를 반환하면 data는 BackendErrorResponse 타입이라고 컴파일러에게 힌트를 준다.
function isBackendErrorResponse(data: unknown): data is BackendErrorResponse {
  return (
    typeof data === 'object' &&
    data !== null &&
    'code' in data &&
    'message' in data &&
    'status' in data
  );
}

// 3. 토큰 갱신 로직 정의 (라이브러리가 401 에러를 감지하면 이 함수를 호출해준다.)
const refreshAuthLogic = async (failedRequest: AxiosError) => {
  console.log('Attempting to refresh token...');
  try {
    await axios.post<{ accessToken: string }>(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auth/refresh`,
      {},
      { withCredentials: true }
    );

    const newAccessToken = getCookie('accessToken');
    // 실패했던 원래 요청의 헤더를 새 토큰으로 교체한다.
    if (failedRequest.response) {
      failedRequest.response.config.headers['Authorization'] = `Bearer ${newAccessToken}`;
    }

    console.log('Token refreshed successfully. Retrying original request.');

    // Promise.resolve()를 호출하여 라이브러리에게 갱신이 성공했음을 알린다.
    return Promise.resolve();
  } catch (refreshError) {
    console.error('Token refresh failed:', refreshError);
    const { logout } = useAuthStore.getState();

    logout();

    if (typeof window !== 'undefined') {
      window.location.href = PATHS.LOGIN;
    }

    // Promise.reject()를 호출하여 라이브러리에게 갱신이 실패했음을 알리고,
    // 원래 요청의 프로미스도 reject 상태로 만든다.
    return Promise.reject(refreshError);
  }
};

createAuthRefreshInterceptor(apiClient, refreshAuthLogic, {
  statusCodes: [401],
});

export default apiClient;
