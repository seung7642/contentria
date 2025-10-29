import type { Metadata } from 'next';
import BlogHeader from '@/components/blog/header';
import TableOfContents from '@/components/blog/TableOfContents';
import { getBlogLayout } from '@/services/server/blogService';
import Footer from '@/components/home/Footer';

export const metadata: Metadata = {
  title: 'Blog',
  description: 'Read this interesting blog post.',
};

interface BlogLayoutProps {
  children: React.ReactNode;
  sidebar: React.ReactNode; // @sidebar 슬롯이 props로 돌아온다.
  params: Promise<{ blogSlug: string }>;
}

export default async function BlogLayout({ children, sidebar, params }: BlogLayoutProps) {
  const pageHeadings: { id: string; text: string; level: number }[] = [];
  const { blogSlug } = await params;
  const layoutData = await getBlogLayout(blogSlug);
  const blogName = layoutData?.blog.slug ?? 'Blog';

  return (
    <div className="grid h-screen grid-rows-[auto_1fr_auto]">
      <BlogHeader blogName={blogName} blogSlug={blogSlug} />

      {/* 중간 영역*/}
      <div className="grid grid-cols-[auto_minmax(0,1fr)_auto]">
        {/* 사이드바 */}
        {sidebar}

        {/* 메인 콘텐츠: 최대 너비 제한 */}
        <main className="m-4 mx-auto w-full max-w-4xl overflow-auto border border-gray-400 p-4">
          {children}
        </main>

        {/* 목차 */}
        <TableOfContents headings={pageHeadings}></TableOfContents>
      </div>

      <Footer />
    </div>
  );
}
