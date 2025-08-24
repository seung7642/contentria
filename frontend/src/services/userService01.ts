import apiClient01 from '@/lib/apiClient01';
import { AuthError } from '@/types/api/errors';
import { User } from '@/types/user';
import axios from 'axios';

async function withAuthErrorHandling<T>(
  apiCall: () => Promise<T>,
  defaultErrorMessage: string = 'An unexpected authentication operation error occurred'
): Promise<T> {
  try {
    return await apiCall();
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      // Axios 에러인 경우, 응답 데이터에서 상세 에러 정보를 추출 시도
      const errorData = error.response?.data;
      const message = errorData?.message || error.message || defaultErrorMessage;
      throw new AuthError(message, error.response?.status || 500, errorData);
    } else if (error instanceof Error) {
      throw new AuthError(error.message || defaultErrorMessage, 500);
    } else {
      throw new AuthError(String(error) || defaultErrorMessage, 0);
    }
  }
}

export const userService = {
  async getMe(): Promise<User> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient01.get<User>('/api/users/me');
      return data;
    }, 'Failed to get user information');
  },
};
