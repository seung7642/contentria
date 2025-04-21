import type { Metadata } from 'next';
import type { ReactNode } from 'react';
import localFont from 'next/font/local';
import './globals.css';
import AuthInitializer from '@/components/common/AuthInitializer';
import { User } from '@/store/authStore';
import { cookies } from 'next/headers';

const pretendard = localFont({
  src: '../../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  weight: '45 920',
  variable: '--font-pretendard',
});

// 기본 메타데이터 설정 (애플리케이션 전체에 적용)
// 하위 페이지/레이아웃에서 구체적인 내용으로 덮어쓰거나 확장할 수 있다.
export const metadata: Metadata = {
  title: {
    template: '%s | Contentria',
    default: 'Contentria',
  },
  description: 'Contentria is a blog platform',
};

async function getUserData(): Promise<User | null> {
  const AUTH_COOKIE_NAME = 'auth_token';
  const cookieStore = cookies();
  const token = cookieStore.get(AUTH_COOKIE_NAME)?.value;

  if (!token) {
    return null;
  }

  try {
    const response = await fetch('http://localhost:8080/api/users/me', {
      headers: { Authorization: `Bearer ${token}` },
      cache: 'no-store',
    });
    if (response.ok) {
      return await response.json();
    }
    return null;
  } catch (e) {
    console.error('RootLayout: Error fetching user data:', e);
    return null;
  }
}

export default async function RootLayout({ children }: { children: ReactNode }) {
  // 서버 렌더링 시 사용자 정보 조회 시도
  const initialUser = await getUserData();
  console.log('RootLayout: Initial user data:', initialUser);

  return (
    <html lang="ko">
      <body className={pretendard.className}>
        <AuthInitializer initialUser={initialUser} />
        {children}
      </body>
    </html>
  );
}
