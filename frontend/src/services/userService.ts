import apiClient from '@/lib/apiClient';
import { User } from '@/types/user';

export const userService = {
  async getMe(): Promise<User> {
    const { data } = await apiClient.get<User>('/api/users/me');
    return data;
  },
};
