'use client';

import React, { useEffect } from 'react';

import DashboardHeader from '@/components/dashboard/DashboardHeader';
import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import { useRouter } from 'next/navigation';
import { useUserQuery } from '@/hooks/queries/useUserQuery';
import { PATHS } from '@/constants/paths';
import { Loader2 } from 'lucide-react';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const router = useRouter();
  const { data: user, isPending: isUserLoading, isError: isUserError } = useUserQuery();

  useEffect(() => {
    if (!isUserLoading && (isUserError || !user)) {
      router.replace(PATHS.LOGIN);
    }
  }, [isUserLoading, isUserError, user, router]);

  if (isUserLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-indigo-600" />
      </div>
    );
  }

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
