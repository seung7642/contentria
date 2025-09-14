import { ApiError } from '@/errors/ApiError';
import { CreateBlogPayload, UpdateProfilePayload, userService } from '@/services/userService';
import { User } from '@/types/user';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';

export const useUpdateProfileMutation = () => {
  return useMutation<User, ApiError | AxiosError, UpdateProfilePayload>({
    mutationFn: (payload) => userService.updateProfile(payload),
  });
};

export const useCreateBlogMutation = () => {
  const queryClient = useQueryClient();

  return useMutation<User, ApiError | AxiosError, CreateBlogPayload>({
    mutationFn: (payload) => userService.createBlog(payload),
    onSuccess: (updatedUser) => {
      // 뮤테이션 성공 시, 서버로부터 받은 최신 user 데이터로
      // 'user' 쿼리의 캐시를 직접 업데이트한다.
      // 이렇게 하면 useUserQuery를 사용하는 모든 컴포넌트가 자동으로 리렌더링된다.
      queryClient.setQueryData(['user'], updatedUser);
    },
    onError: (error) => {
      console.error('Failed to create blog:', error);
    },
  });
};
