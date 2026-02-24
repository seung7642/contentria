import React from 'react';

import DashboardHeader from '@/components/dashboard/DashboardHeader';
import DashboardSidebar from '@/components/dashboard/DashboardSidebar';
import Footer from '@/components/home/Footer';
import { getUserProfileAction } from '@/actions/user';
import { getMyBlogAction } from '@/actions/blog';

export default async function DashboardLayout({ children }: { children: React.ReactNode }) {
  const user = await getUserProfileAction();
  const blogInfos = await getMyBlogAction();
  const hasBlogs = blogInfos && blogInfos.length > 0;
  const firstBlogSlug = hasBlogs ? blogInfos[0].slug : null;

  if (!user) {
    console.log('No user found, rendering nothing.');
    return null;
  }

  return (
    <div className="grid h-screen grid-rows-[auto_1fr_auto] bg-gray-50">
      <DashboardHeader blogSlug={firstBlogSlug} />

      <div className="mx-auto grid w-full max-w-7xl grid-cols-1 gap-6 p-4 md:grid-cols-[auto_1fr]">
        <DashboardSidebar blogInfos={blogInfos} />
        <main className="overflow-y-auto p-6 shadow-sm">{children}</main>
      </div>

      <Footer />
    </div>
  );
}
