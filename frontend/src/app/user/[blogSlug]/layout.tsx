import type { Metadata } from 'next';
import BlogHeader from '@/components/blog/header';
import TableOfContents from '@/components/blog/TableOfContents';
import { getBlogLayout } from '@/services/server/blogService';
import Footer from '@/components/home/Footer';

export const metadata: Metadata = {
  title: 'Blog',
  description: 'Read this interesting blog post.',
};

export default async function BlogLayout({
  children,
  sidebar, // @sidebar 슬롯이 props로 돌아온다.
  params,
}: {
  children: React.ReactNode;
  sidebar: React.ReactNode;
  params: { blogSlug: string };
}) {
  const pageHeadings: { id: string; text: string; level: number }[] = [];
  const layoutData = await getBlogLayout(params.blogSlug);
  const blogName = layoutData?.blog.slug ?? 'Blog';

  return (
    <div className={`grid h-screen grid-rows-[auto_1fr_auto]`}>
      <BlogHeader blogName={blogName} blogSlug={params.blogSlug} />
      <div className="grid grid-cols-[auto_minmax(auto,_1200px)_auto]">
        {sidebar}
        <main className="mx-auto w-full max-w-4xl overflow-auto p-4">
          <div className="flex-grow p-4">{children}</div>
        </main>
        <TableOfContents headings={pageHeadings}></TableOfContents>
      </div>
      <Footer />
    </div>
  );
}
