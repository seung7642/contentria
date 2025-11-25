import type { Metadata } from 'next';
import BlogHeader from '@/components/blog/header';
import TableOfContents from '@/components/blog/TableOfContents';
import { getBlogLayout } from '@/services/blogService';
import Footer from '@/components/home/Footer';

export const metadata: Metadata = {
  title: 'Blog',
  description: 'Read this interesting blog post.',
};

interface BlogLayoutProps {
  children: React.ReactNode;
  sidebar: React.ReactNode; // @sidebar 슬롯이 props로 돌아온다.
  params: { blogSlug: string };
}

/**
 * props로 sidebar slots가 전달된다. 참고로, children도 slots이다.
 * - https://nextjs.org/docs/app/api-reference/file-conventions/parallel-routes#slots
 */
export default async function BlogLayout({ children, sidebar, params }: BlogLayoutProps) {
  const pageHeadings: { id: string; text: string; level: number }[] = [];
  const { blogSlug } = params;
  const layoutData = await getBlogLayout(blogSlug);
  const blogName = layoutData?.blog.slug ?? 'Blog';

  return (
    <>
      <div className="flex min-h-screen flex-col">
        <BlogHeader blogName={blogName} blogSlug={blogSlug} />

        {/* 중간 영역*/}
        <div className="grid flex-1 grid-cols-[auto_minmax(0,1fr)_auto]">
          {/* 사이드바 */}
          {sidebar}

          {/* 메인 콘텐츠: 최대 너비 제한 */}
          <main className="w-full px-4 py-6">{children}</main>
        </div>
      </div>
      <Footer />
    </>
  );
}
