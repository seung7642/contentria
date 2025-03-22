'use client';

import { useEffect, useState } from 'react';
import type { Metadata } from 'next';
import localFont from 'next/font/local';
import '@/app/globals.css';
import DashboardSidebar from '@/components/dashboard/dashboardSidebar';
import DashboardHeader from '@/components/dashboard/dashboardHeader';
import AccessDenied from '@/components/dashboard/accessDenied';

const pretendard = localFont({
  src: '../../../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  weight: '45 920',
});

// export const metadata: Metadata = {
//   title: '블로그 관리',
//   description: '블로그 관리 대시보드',
// };

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const [isAuthorized, setIsAuthorized] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userEmail, setUserEmail] = useState<string>('');

  useEffect(() => {
    // 사용자 권한 확인
    const checkAuthorization = () => {
      try {
        const userData = localStorage.getItem('userData');

        if (userData) {
          const { email } = JSON.parse(userData);
          setUserEmail(email);
          console.log(`이메일: ${userEmail}`);
          console.log(email === 'seung7642@gmail.com');

          // 특정 이메일 사용자에게만 권한 부여
          setIsAuthorized(email === 'seung7642@gmail.com');
        } else {
          setUserEmail('로그인되지 않은 사용자');
          setIsAuthorized(false);
        }
      } catch (error) {
        console.error('사용자 정보 로드 실패:', error);
        setUserEmail('오류 발생');
        setIsAuthorized(false);
      } finally {
        setIsLoading(false);
      }
    };

    // CSR에서만 실행
    if (typeof window !== 'undefined') {
      checkAuthorization();
    }
  }, []);

  return (
    <html lang="ko">
      <body className={`${pretendard.className} bg-gray-50`}>
        <div className="flex min-h-screen flex-col">
          <DashboardHeader />
          <div className="mx-auto mt-10 w-full max-w-7xl flex-1">
            {isLoading ? (
              <div className="flex h-48 w-full items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
                <span className="ml-3">로딩 중...</span>
              </div>
            ) : isAuthorized ? (
              <div className="flex flex-col justify-center md:flex-row">
                <DashboardSidebar />
                <div className="max-w-5xl flex-1 px-4 py-6 md:px-6">{children}</div>
              </div>
            ) : (
              // 권한이 없는 경우 접근 거부 화면 표시
              <div className="w-full px-4 py-6">
                <AccessDenied userEmail={userEmail} />
              </div>
            )}
          </div>
        </div>
      </body>
    </html>
  );
}
