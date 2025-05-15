import { DEFAULT_LOGGED_IN_REDIRECT_URL } from '@/constants/auth';
import { authService } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

export const useAuthRedirect = () => {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  useEffect(() => {
    const checkAuthStatus = async () => {
      if (user) {
        router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
        return;
      }
      try {
        const fetchedUser = await authService.getMe();
        setUser(fetchedUser);
        router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
      } catch (error) {
        setIsCheckingAuth(false);
      }
    };
    checkAuthStatus();
  }, [router, user, setUser]);

  return isCheckingAuth;
};
