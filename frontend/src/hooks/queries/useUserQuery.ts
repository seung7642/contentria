import { ApiError } from '@/types/api/errors';
import { userService } from '@/services/userService';
import { User } from '@/types/user';
import { useQuery } from '@tanstack/react-query';

export const useUserQuery = () => {
  return useQuery<User, ApiError>({
    queryKey: ['user', 'me'], // 쿼리를 식별하는 고유한 키
    queryFn: userService.getMe, // 데이터를 가져오는 함수
    retry: false,
    refetchOnWindowFocus: false, // 윈도우 포커스 시 리페칭 여부
  });
};
