'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { userService } from '@/services/userService';
import { Loader2 } from 'lucide-react';

const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  // 1. 스토어에서 필요한 상태(isLoading)와 액션(initializeAuth)을 가져온다.
  const isLoading = useAuthStore((state) => state.isLoading);
  const initializeAuth = useAuthStore((state) => state.initializeAuth);

  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const user = await userService.getMe();
        initializeAuth(user);
      } catch (error) {
        console.error('[AuthInitializer] Authentication check failed:', error);
        initializeAuth(null);
      }
    };

    checkAuthStatus();
  }, [initializeAuth]); // initializeAuth는 Zustand에 의해 안정성이 보장되므로, 이 useEffect는 의도대로 한 번만 실행된다.

  if (isLoading) {
    return <Loader2 className="h-12 w-12 animate-spin text-indigo-600" />;
  }

  return <>{children}</>;
};

export default AuthInitializer;
