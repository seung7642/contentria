import React from 'react';

import DashboardHeader from '@/components/dashboard/dashboardHeader';
import DashboardSidebar from '@/components/dashboard/dashboardSidebar';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
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
