import apiClient from '@/lib/apiClient';
import { ApiError, ErrorResponse } from '@/types/api/errors';
import { ApiResult } from '@/types/api/result';
import { User } from '@/types/user';
import axios from 'axios';

async function withAuthErrorHandling<T>(apiCall: () => Promise<T>): Promise<ApiResult<T>> {
  try {
    const data = await apiCall();
    return { success: true, data };
  } catch (error) {
    let apiError: ApiError;

    if (axios.isAxiosError(error) && error.response?.data) {
      apiError = new ApiError(error.response.data as ErrorResponse);
    } else {
      // 네트워크 에러, 타임아웃 또는 서버가 죽어서 JSON 응답조차 못 주는 경우
      // 프론트엔드에서 자체적으로 비상용 ErrorResponse 객체를 만들어 ApiError로 정규화한다.
      const fallbackResponse: ErrorResponse = {
        timestamp: new Date().toISOString(),
        status: 0,
        error: '',
        message:
          error instanceof Error ? error.message : 'An unexpected client-side error occurred.',
        path: '',
        code: 'CLIENT0000',
      };
      apiError = new ApiError(fallbackResponse);
    }

    return { success: false, error: apiError };
  }
}

export const userService = {
  async getMe(): Promise<ApiResult<User>> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient.get<User>('/api/users/me');
      return data;
    });
  },
};
