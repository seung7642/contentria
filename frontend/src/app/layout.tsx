import type { Metadata } from 'next';
import localFont from 'next/font/local';
import './globals.css';
import AuthInitializer from '@/components/auth/AuthInitializer';
import Providers from '@/components/Providers';
import { getUserProfileAction } from '@/actions/user';
import { User } from '@/types/api/user';

const pretendard = localFont({
  src: '../../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  weight: '45 920',
  variable: '--font-pretendard',
});

export const metadata: Metadata = {
  title: {
    template: '%s | Contentria',
    default: 'Contentria',
  },
  description: 'Contentria is a blog platform',
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  let initialUser: User | null = null;

  try {
    initialUser = await getUserProfileAction(false);
  } catch (error) {
    console.error('[RootLayout] Failed to fetch initial user profile:', error);
    initialUser = null;
  }

  return (
    <html lang="ko">
      <body className={pretendard.className}>
        <Providers>
          <AuthInitializer initialUser={initialUser}>{children}</AuthInitializer>
        </Providers>
      </body>
    </html>
  );
}
