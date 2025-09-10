'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { userService } from '@/services/userService';
import { ApiError } from '@/errors/ApiError';

const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const logout = useAuthStore((state) => state.logout);

  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        const user = await userService.getMe();
        console.log('[Auth] User authenticated successfully.');
        initializeAuth(user);
      } catch (err) {
        if (err instanceof ApiError && err.status === 401) {
          console.log('[Auth] No active session found. Initializing as guest.');
          logout();
        } else {
          console.error('[Auth] An unexpected error occurred during auth check:', err);
        }
        initializeAuth(null);
      }
    };

    checkAuthStatus();
  }, [initializeAuth, logout]);

  return <>{children}</>;
};

export default AuthInitializer;
