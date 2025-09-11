'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useUserQuery } from '@/hooks/queries/useUserQuery';

const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  const initializeAuth = useAuthStore((state) => state.initializeAuth);

  // 1. 커스텀 훅을 호출하여 서버 상태를 가져온다.
  //    isLoading, isError, data, error 등 모든 상태를 TanStack Query가 관리해준다.
  const { data: user, isSuccess, isError, error } = useUserQuery();

  useEffect(() => {
    // 2. TanStack Query의 상태가 변경될 때마다 이 useEffect가 실행된다.
    if (isSuccess) {
      console.log('[Auth] TanStack Query check successful. User authenticated.');
      initializeAuth(user);
    } else if (isError) {
      // 쿼리가 에러와 함께 완료되면, 에러 종류에 따라 상태를 초기화한다.
      if (error?.status === 401) {
        console.log('[Auth] TanStack Query check failed with 401. Initializing as guest.');
        initializeAuth(null);
      } else {
        // 그 외 네트워크 에러 등은 콘솔에 기록한다.
        console.error('[AuthInitializer] An unexpected error occurred via TanStack Query:', error);
        initializeAuth(null);
      }
    }
  }, [initializeAuth, isSuccess, isError, user, error]);

  return <>{children}</>;
};

export default AuthInitializer;
