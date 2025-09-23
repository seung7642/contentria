import type { Metadata } from 'next';
import { Footer } from '@/components/blog/Footer';
import BlogHeader from '@/components/blog/header';
import Sidebar from '@/components/blog/Sidebar';
import TableOfContents from '@/components/blog/TableOfContents';

export const metadata: Metadata = {
  title: 'Blog',
  description: 'Read this interesting blog post.',
};

export default function BlogLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const pageHeadings: { id: string; text: string; level: number }[] = [];

  return (
    <div className={`grid h-screen grid-rows-[auto_1fr_auto]`}>
      <BlogHeader />
      <div className="grid grid-cols-[auto_minmax(auto,_1200px)_auto]">
        <Sidebar />
        <main className="mx-auto w-full max-w-4xl overflow-auto p-4">
          <div className="flex-grow p-4">{children}</div>
        </main>
        <TableOfContents headings={pageHeadings}></TableOfContents>
      </div>
      <Footer />
    </div>
  );
}
