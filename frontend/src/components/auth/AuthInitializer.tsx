'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { userService } from '@/services/userService';
import { Loader2 } from 'lucide-react';

const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  // 1. 스토어에서 필요한 상태(isLoading)와 액션(initializeAuth)을 가져온다.
  const isLoading = useAuthStore((state) => state.isLoading);
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const logout = useAuthStore((state) => state.logout);

  useEffect(() => {
    const checkAuthStatus = async () => {
      const result = await userService.getMe();
      if (result.success) {
        console.log('[Auth] User authenticated successfully.');
        initializeAuth(result.data);
      } else {
        const { error } = result;

        if (error.status === 401) {
          console.log('[Auth] No active session found. Initializing as guest.');
          logout();
        } else {
          console.error(
            `[AuthInitializer] An unexpected error occurred during auth check:`,
            error.details
          );
        }

        initializeAuth(null);
      }
    };

    checkAuthStatus();
  }, [initializeAuth, logout]);

  if (isLoading) {
    return <Loader2 className="h-12 w-12 animate-spin text-indigo-600" />;
  }

  return <>{children}</>;
};

export default AuthInitializer;
