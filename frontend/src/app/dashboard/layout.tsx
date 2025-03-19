import type { Metadata } from 'next';
import '@/app/globals.css';
import DashboardSidebar from '@/components/dashboard/dashboardSidebar';
import DashboardHeader from '@/components/dashboard/dashboardHeader';
import localFont from 'next/font/local';

const pretendard = localFont({
  src: '../../../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  weight: '45 920',
});

export const metadata: Metadata = {
  title: '블로그 관리',
  description: '블로그 관리 대시보드',
};

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body className={`${pretendard.className} bg-gray-50`}>
        <div className="flex min-h-screen flex-col">
          <DashboardHeader />
          <div className="mx-auto mt-10 w-full max-w-7xl flex-1">
            <div className="flex flex-col justify-center md:flex-row">
              <DashboardSidebar />
              <div className="max-w-5xl flex-1 px-4 py-6 md:px-6">{children}</div>
            </div>
          </div>
        </div>
      </body>
    </html>
  );
}
