'use client';

import React, { useEffect } from 'react';

import DashboardHeader from '@/components/dashboard/DashboardHeader';
import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import { usePathname, useRouter } from 'next/navigation';
import { PATHS } from '@/constants/paths';
import { useAuthStore } from '@/store/authStore';
import Footer from '@/components/home/Footer';
import HomeHeader from '@/components/home/homeHeader/HomeHeader';

interface DashboardLayoutProps {
  children: React.ReactNode;
}

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  const pathname = usePathname();
  const router = useRouter();
  const user = useAuthStore((state) => state.user);

  useEffect(() => {
    if (!user) {
      router.replace(PATHS.LOGIN);
    }
  }, [user, router]);

  const isEditorMode = pathname.startsWith('/dashboard/posts/new');

  if (isEditorMode) {
    return (
      <>
        <div className="flex min-h-screen flex-col bg-gray-50 antialiased">
          <DashboardHeader />
          <main className="flex flex-1 flex-col">{children}</main>
        </div>
        <Footer />
      </>
    );
  }

  return (
    <div className="grid h-screen grid-rows-[auto_1fr_auto] bg-gray-50">
      {/* 1. 헤더 */}
      <DashboardHeader />

      {/* 2. 중앙 컨텐츠 (사이드바 + 메인) */}
      <div className="mx-auto grid w-full max-w-7xl grid-cols-1 gap-6 p-4 md:grid-cols-[auto_1fr]">
        {/* 사이드바 영역 */}
        <DashboardSidebar />

        {/* 메인 컨텐츠 영역 */}
        <main className="overflow-y-auto p-6 shadow-sm">{children}</main>
      </div>

      {/* 3. 푸터 */}
      <Footer />
    </div>
  );
}
