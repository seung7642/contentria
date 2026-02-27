import { getMyBlogAction } from '@/actions/blog';
import { getUserProfileAction } from '@/actions/user';
import DashboardHeader from '@/components/dashboard/DashboardHeader';
import Footer from '@/components/home/Footer';

export default async function EditorLayout({ children }: { children: React.ReactNode }) {
  const user = await getUserProfileAction();
  const blogInfos = await getMyBlogAction();
  const hasBlogs = blogInfos && blogInfos.length > 0;
  const firstBlogSlug = hasBlogs ? blogInfos[0].slug : null;

  if (!user) {
    console.log('No user found, rendering nothing.');
    return null;
  }

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
