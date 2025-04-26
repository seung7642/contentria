import type { Metadata } from 'next';
import type { ReactNode } from 'react';
import localFont from 'next/font/local';
import './globals.css';
import AuthInitializer from '@/components/common/AuthInitializer';
import { User } from '@/store/authStore';
import { cookies } from 'next/headers';
import { ACCESS_TOKEN_COOKIE_NAME } from '@/constants/auth';

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

export default async function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="ko">
      <body className={pretendard.className}>{children}</body>
    </html>
  );
}
