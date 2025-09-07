import { PATHS } from '@/constants/paths';
import { useAuthStore } from '@/store/authStore';
import axios, { AxiosError } from 'axios';
import createAuthRefreshInterceptor from 'axios-auth-refresh';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true, // HttpOnly 쿠키(refreshToken)를 주고받기 위한 설정
});

// 1. 요청 인터셉터: 모든 요청에 액세스 토큰을 자동으로 추가한다.
apiClient.interceptors.request.use(
  (config) => {
    // const accessToken = getCookie('accessToken');
    const accessToken = useAuthStore.getState().accessToken;
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. 토큰 갱신 로직 정의 (라이브러리가 401 에러를 감지하면 이 함수를 호출해준다.)
const refreshAuthLogic = async (failedRequest: AxiosError) => {
  console.log('Attempting to refresh token...');
  try {
    const { data } = await axios.post<{ accessToken: string }>(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auth/refresh`,
      {},
      { withCredentials: true }
    );

    const newAccessToken = data.accessToken;
    const { setAccessToken } = useAuthStore.getState();

    setAccessToken(newAccessToken);

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
