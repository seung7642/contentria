import { ApiError } from '@/types/api/errors';
import { userService } from '@/services/userService';
import { User } from '@/types/api/user';
import { useQuery } from '@tanstack/react-query';
import { userKeys } from './keys';
import { getUserProfileAction } from '@/actions/user';

export const useUserQuery = () => {
  return useQuery<User, ApiError>({
    queryKey: ['user', 'me'], // 쿼리를 식별하는 고유한 키
    queryFn: userService.getMe, // 데이터를 가져오는 함수
    retry: false,
    refetchOnWindowFocus: false, // 윈도우 포커스 시 리페칭 여부
  });
};

export const useUserProfile = () => {
  return useQuery({
    queryKey: userKeys.profile(),
    queryFn: () => getUserProfileAction(),
    staleTime: 5 * 60 * 1000, // 5분간 캐시 유지
    retry: 1,
    throwOnError: true, // 에러 발생 시 ErrorBoundary로 전파
  });
};
