import { ApiError } from '@/types/api/errors';
import {
  CreateBlogPayload,
  CreateBlogResponse,
  UpdateProfilePayload,
  userService,
} from '@/services/userService';
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

  return useMutation<CreateBlogResponse, ApiError | AxiosError, CreateBlogPayload>({
    mutationFn: (payload) => userService.createBlog(payload),
    onSuccess: (response) => {
      console.log('Blog created successfully, updating user cache:', response);
      queryClient.invalidateQueries({ queryKey: ['user', 'me'] });
    },
    onError: (error) => {
      console.error('Failed to create blog:', error);
    },
  });
};
