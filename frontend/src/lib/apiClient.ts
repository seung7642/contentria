import { PATHS } from '@/constants/paths';
import { ApiError, ApiErrorResponse } from '@/types/api/errors';
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
    // 401 에러는 토큰 갱신 로직이 처리해야 하므로 건드리지 않고 패스한다.
    // 여기서 ApiError로 변환해버리면 뒤에 있는 axios-auth-refresh가 401 상태 코드를 인식하지 못한다.
    if (error.response?.status === 401) {
      return Promise.reject(error);
    }

    if (error.response && isApiErrorResponse(error.response.data)) {
      // error.response.data는 자동으로 'ApiErrorResponse' 타입으로 추론된다.
      // 'as ApiErrorResponse' 같은 강제 형변환이 필요없다.
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
// 함수가 true를 반환하면 data는 ApiErrorResponse 타입이라고 컴파일러에게 힌트를 준다.
function isApiErrorResponse(data: unknown): data is ApiErrorResponse {
  return (
    typeof data === 'object' &&
    data !== null &&
    'code' in data &&
    'message' in data &&
    'status' in data
  );
}

// ================================================================================
// 3. 토큰 갱신 로직 정의
// ================================================================================

// 한 페이지에 있는 다수의 클라이언트 컴포넌트가 동시에 토큰 갱신 요청 시 Race Condition이 발생할 수 있다.
// 보통 백엔드에선 Refresh Token을 1회 사용하면 무효화(RTR, Refresh Token Rotation)시키기 때문에,
// 여러 컴포넌트에서 동시에 갱신 요청이 가면 그 중 하나만 성공하고 나머지는 실패할 수 있다.
// 이를 방지하기 위해, 토큰 갱신 요청이 진행 중일 땐 다른 컴포넌트들은 대기하도록 구현할 수 있다.
// 아래 예시는 간단한 플래그 변수를 사용한 방법이다.
// 더 정교한 구현이 필요하면 Promise 큐를 만들어서 관리하는 방법도 있다.

// 현재 다른 클라이언트 컴포넌트에서 토큰 갱신 요청이 진행 중인지 추적
let isRefreshing = false;

// 갱신 동안 대기하는 요청들을 담을 큐
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: any) => void;
}> = [];

// 대기열 처리 함수
const processQueue = (error: any, token: string | null | undefined = null) => {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else {
      promise.resolve(token);
    }
  });
  failedQueue = [];
};

// axios-auth-refresh 라이브러리가 401 에러를 감지하면 이 함수를 호출해준다.
const refreshAuthLogic = async (failedRequest: AxiosError) => {
  // 1. [Lock 체크] 이미 갱신 로직이 돌고 있다면? -> 대기열(Queue)에 넣고 대기
  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      failedQueue.push({ resolve, reject });
    })
      .then(() => {
        // 대기열이 해소되면(갱신 성공), 이 요청도 최신 토큰으로 헤더 업데이트 후 재시도되도록 함
        const newAccessToken = getCookie('accessToken');
        if (failedRequest.response?.config.headers) {
          failedRequest.response.config.headers['Authorization'] = `Bearer ${newAccessToken}`;
        }
        return Promise.resolve();
      })
      .catch((err) => {
        // 갱신 실패 시 같이 에러 처리
        return Promise.reject(err);
      });
  }

  // 2. [Lock 설정] 내가 첫 번째 진입자라면 -> 문 잠그고 갱신 시작
  isRefreshing = true;

  try {
    // 리프레시 API 호출 (쿠키는 자동으로 전송됨)
    await axios.post<{ accessToken: string }>(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auth/refresh`,
      {},
      { withCredentials: true }
    );

    // 갱신된 토큰 가져오기 (브라우저 쿠키가 갱신되었을 것임)
    const newAccessToken = getCookie('accessToken') as string | undefined;

    // 실패했던 원래 요청의 헤더를 새 토큰으로 교체한다.
    if (failedRequest.response) {
      failedRequest.response.config.headers['Authorization'] = `Bearer ${newAccessToken}`;
    }

    console.log('Token refreshed successfully. Retrying original request.');

    // 3. [대기열 해소] 기다리던 다른 요청들에게 "성공했다"고 알림
    processQueue(null, newAccessToken);

    // Promise.resolve()를 호출하여 라이브러리에게 갱신이 성공했음을 알린다.
    return Promise.resolve();
  } catch (refreshError) {
    console.error('Token refresh failed:', refreshError);

    // 4. [실패 처리] 대기하던 요청들도 모두 에러 처리
    processQueue(refreshError, null);

    const { logout } = useAuthStore.getState();
    logout();

    if (typeof window !== 'undefined') {
      window.location.href = PATHS.LOGIN;
    }

    // Promise.reject()를 호출하여 라이브러리에게 갱신이 실패했음을 알리고, 원래 요청의 프로미스도 reject 상태로 만든다.
    return Promise.reject(refreshError);
  } finally {
    // 5. [Lock 해제] 모든 처리가 끝났으므로 잠금 해제
    isRefreshing = false;
  }
};

// axios-auth-refresh를 사용하여 인터셉터 등록
createAuthRefreshInterceptor(apiClient, refreshAuthLogic, {
  statusCodes: [401],
});

export default apiClient;
