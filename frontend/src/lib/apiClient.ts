import axios, { AxiosError } from 'axios';
import { getCookie, setCookie } from 'cookies-next';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
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

// 2. 응답 인터셉터: 401 에러 발생 시 토큰 재발급 및 재요청 로직
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;

    // 401 에러이고, 재시도한 요청이 아닐 경우에만 실행
    if (error.response?.status == 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true; // 무한 재시도 방지를 위한 플래그

      try {
        // 리프레스 토큰으로 새 액세스 토큰을 요청하는 API 호출
        const refreshToken = getCookie('refreshToken');
        const { data } = await axios.post<{ accessToken: string }>(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/auth/refresh`,
          { refreshToken }
        );

        const newAccessToken = data.accessToken;

        // 새로 발급받은 토큰을 쿠키에 저장
        setCookie('accessToken', newAccessToken);

        // axios 인스턴스의 기본 헤더와 실패했던 운래 요청의 헤더를 새 토큰으로 변경
        axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

        // 실패했던 원래 요청을 다시 실행
        return apiClient(originalRequest);
      } catch (refreshError) {
        console.info('Token refresh failed. Please try logging in again.', refreshError);
        return Promise.reject(refreshError);
      }
    }

    // 401 에러가 아니거나, 재시도에 실패한 경우
    return Promise.reject(error);
  }
);

export default apiClient;
