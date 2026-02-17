'use client';

import { ApiError } from '@/types/api/errors';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { createBlogAction } from '@/actions/blog';
import { CreateBlogPayload, CreateBlogResponse } from '@/types/api/blogs';

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
