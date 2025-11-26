import apiClient from '@/lib/apiClient';
import { User } from '@/types/api/user';

export interface UpdateProfilePayload {
  name: string;
}

export interface CreateBlogPayload {
  slug: string;
}

export interface CreateBlogResponse {
  id: string;
  slug: string;
  title: string;
  description: string | null;
  createdAt: string;
}

export const userService = {
  async getMe(): Promise<User> {
    const { data } = await apiClient.get<User>('/api/users/me');
    return data;
  },

  async updateProfile(payload: UpdateProfilePayload): Promise<User> {
    // const { data } = await apiClient.patch<User>('/api/users/me', payload);
    // return data;

    // --- 임시 모의(mock) API 응답 ---
    return new Promise((resolve) =>
      setTimeout(() => {
        // 실제 API는 업데이트된 전체 사용자 객체를 반환해야 합니다.
        resolve({
          id: 'mock-user-id-123',
          name: payload.name, // 요청받은 새 이름으로 업데이트하여 반환
          email: 'seung7642@gmail.com', // 이메일은 변경되지 않음
          profileImage: null, // 프로필 이미지는 변경되지 않음
          slugs: [],
        });
      }, 500)
    );
  },

  async createBlog(payload: CreateBlogPayload): Promise<CreateBlogResponse> {
    const { data } = await apiClient.post<CreateBlogResponse>('/api/blogs/create', payload);
    return data;
  },
};
