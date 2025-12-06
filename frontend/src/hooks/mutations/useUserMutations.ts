'use client';

import { ApiError } from '@/types/api/errors';
import { UpdateProfilePayload, User } from '@/types/api/user';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { updateUserProfileAction } from '@/actions/user';
import { userKeys } from '../queries/keys';
import { createBlogAction } from '@/actions/blog';
import { CreateBlogPayload, CreateBlogResponse } from '@/types/api/blogs';

export const useUpdateUserProfileMutation = () => {
  const queryClient = useQueryClient();

  return useMutation<
    User, // TData: 성공 시 반환되는 데이터 타입
    Error, // TError: ★ 중요! 서버 액션 에러는 기본 Error 타입임
    UpdateProfilePayload // TVariables: mutate 함수에 넣을 파라미터 타입
  >({
    // `(payload) => updateUserProfileAction(payload)`와 동일
    mutationFn: updateUserProfileAction,
    onSuccess: (updatedUser) => {
      console.log('User profile updated successfully:', updatedUser);

      queryClient.invalidateQueries({ queryKey: userKeys.profile() });
    },
    onError: (error) => {
      console.error('Failed to update user profile:', error);
      // 토스트 메시지 등을 띄울 수 있다.
    },
  });
};

export const useCreateBlogMutation = () => {
  const queryClient = useQueryClient();

  return useMutation<CreateBlogResponse, ApiError | AxiosError, CreateBlogPayload>({
    mutationFn: (payload) => createBlogAction(payload),
    onSuccess: (response) => {
      console.log('Blog created successfully, updating user cache:', response);
      queryClient.invalidateQueries({ queryKey: ['user', 'me'] });
    },
    onError: (error) => {
      console.error('Failed to create blog:', error);
    },
  });
};
