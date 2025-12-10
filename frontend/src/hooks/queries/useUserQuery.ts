'use client';

import { useQuery } from '@tanstack/react-query';
import { userKeys } from './keys';
import { getUserProfileAction } from '@/actions/user';

export const useUserProfile = () => {
  return useQuery({
    queryKey: userKeys.profile(),
    queryFn: () => getUserProfileAction(false),
    staleTime: 5 * 60 * 1000, // 5분간 캐시 유지
    retry: 0,
  });
};
