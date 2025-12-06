'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useUserQuery } from '@/hooks/queries/useUserQuery';
import { ApiError } from '@/types/api/errors';

const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const { data: fetchedUser, isSuccess, isError, error } = useUserQuery();

  // TanStack Query의 상태가 변경될 때마다 이 useEffect가 실행된다.
  useEffect(() => {
    if (isSuccess && fetchedUser) {
      console.log('[Auth] TanStack Query check successful. User authenticated.');
      initializeAuth(fetchedUser);
    } else if (isError) {
      const status = (error as ApiError)?.status || 500;

      if (status === 401) {
        console.log('[Auth] TanStack Query check failed with 401. Initializing as guest.');
        initializeAuth(null);
      } else {
        console.error('[AuthInitializer] An unexpected error occurred via TanStack Query:', error);
        initializeAuth(null);
      }
    }
  }, [initializeAuth, isSuccess, isError, fetchedUser, error]);

  return <>{children}</>;
};

export default AuthInitializer;
