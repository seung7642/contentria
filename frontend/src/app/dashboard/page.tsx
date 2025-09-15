'use client';

import { useUserQuery } from '@/hooks/queries/useUserQuery';
import DashboardContent from '@/components/dashboard/DashboardContent';
import CreateBlogWelcome from '@/components/dashboard/CreateBlogWelcome';
import { Loader2 } from 'lucide-react';

const DashboardPage = () => {
  const { data: user, isPending: isUserLoading } = useUserQuery();

  if (isUserLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
      </div>
    );
  }

  return user?.slugs?.length ? <DashboardContent /> : <CreateBlogWelcome />;
};

export default DashboardPage;
