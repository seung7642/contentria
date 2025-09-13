'use client';

import { PATHS } from '@/constants/paths';
import { useUserQuery } from '@/hooks/queries/useUserQuery';
import { useAuthStore } from '@/store/authStore';
import { Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

const GoogleLoginCallbackPage = () => {
  const router = useRouter();
  const initializeAuth = useAuthStore((state) => state.initializeAuth);

  // 이 페이지에 도착했다는 것은, 브라우저에 인증 쿠키가 있다는 의미이다.
  // 따라서 useUserQuery를 호출하면 성공적으로 사용자 정보를 가져올 수 있다.
  const { data: user, isSuccess, isError, error } = useUserQuery();

  useEffect(() => {
    if (isSuccess && user) {
      console.log('[Auth Callback] Successfully fetched user, initializing auth state.');
      initializeAuth(user);
    } else if (isError) {
      console.error('[Auth Callback] Failed to fetch user after OIDC login:', error);
      router.replace(PATHS.LOGIN);
    }
  }, [isSuccess, isError, user, error, initializeAuth, router]);

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50">
      <Loader2 className="h-12 w-12 animate-spin text-indigo-600" />
      <p className="mt-4 text-lg text-gray-700">Signing you in...</p>
    </div>
  );
};

export default GoogleLoginCallbackPage;
