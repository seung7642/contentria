import type { Metadata } from 'next';
import BlogHeader from '@/components/blog/header';
import Footer from '@/components/home/Footer';
import { getBlogLayoutAction } from '@/actions/blog';

export const metadata: Metadata = {
  title: 'Blog',
  description: 'Read this interesting blog post.',
};

interface BlogLayoutProps {
  children: React.ReactNode;
  sidebar: React.ReactNode; // @sidebar 슬롯이 props로 돌아온다.
  params: Promise<{ blogSlug: string }>;
}

export async function generateMetadata({ params }: BlogLayoutProps): Promise<Metadata> {
  const { blogSlug } = await params;
  const layoutData = await getBlogLayoutAction(blogSlug);

  return {
    title: layoutData?.blog.title || 'Blog',
    description: layoutData?.blog.description || 'Welcome to my blog',
    openGraph: {
      title: layoutData?.blog.title || 'Blog',
      description: layoutData?.blog.description || 'Welcome to my blog',
    },
  };
}

/**
 * props로 sidebar slots가 전달된다. 참고로, children도 slots이다.
 * - https://nextjs.org/docs/app/api-reference/file-conventions/parallel-routes#slots
 */
export default async function BlogLayout({ children, sidebar, params }: BlogLayoutProps) {
  const { blogSlug } = await params;
  const layoutData = await getBlogLayoutAction(blogSlug);

  return (
    <>
      <div className="flex min-h-screen flex-col">
        <BlogHeader blogTitle={layoutData?.blog.title} blogSlug={blogSlug} />

        <div className="grid flex-1 grid-cols-[auto_minmax(0,1fr)_auto]">
          {sidebar}

          <main className="w-full px-4 py-6">{children}</main>
        </div>
      </div>
      <Footer />
    </>
  );
}
