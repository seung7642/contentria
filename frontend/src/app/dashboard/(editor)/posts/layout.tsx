import { redirect } from 'next/navigation';
import { getMyBlogAction } from '@/actions/blog';
import DashboardHeader from '@/components/dashboard/DashboardHeader';
import Footer from '@/components/home/Footer';

export default async function EditorLayout({ children }: { children: React.ReactNode }) {
  const blogInfos = await getMyBlogAction();
  const hasBlogs = blogInfos && blogInfos.length > 0;

  // Same guard as (main): the editor is meaningless without a blog (direct-URL defense).
  if (!hasBlogs) {
    redirect('/dashboard/welcome');
  }

  const firstBlogSlug = blogInfos![0].slug;

  return (
    <>
      <div className="flex min-h-screen flex-col bg-gray-50 antialiased">
        <DashboardHeader blogSlug={firstBlogSlug} />
        <main className="flex flex-1 flex-col">{children}</main>
      </div>
      <Footer />
    </>
  );
}
