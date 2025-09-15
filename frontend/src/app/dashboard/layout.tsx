'use client';

import React, { useEffect } from 'react';

import DashboardHeader from '@/components/dashboard/DashboardHeader';
import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import { useRouter } from 'next/navigation';
import { PATHS } from '@/constants/paths';
import { useAuthStore } from '@/store/authStore';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);

  useEffect(() => {
    if (!user) {
      router.replace(PATHS.LOGIN);
    }
  }, [user, router]);

  return (
    <div className={`flex min-h-screen flex-col bg-gray-50`}>
      <DashboardHeader />
      <div className="mx-auto mt-10 w-full max-w-7xl flex-1">
        <div className="flex flex-col justify-center md:flex-row">
          <DashboardSidebar />
          <div className="max-w-5xl flex-1 px-4 py-6 md:px-6">{children}</div>
        </div>
      </div>
    </div>
  );
}
