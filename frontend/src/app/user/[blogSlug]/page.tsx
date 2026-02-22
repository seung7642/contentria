import { getBlogLayoutAction } from '@/actions/blog';
import { getBlogPostsAction } from '@/actions/post';
import CustomPagination from '@/components/common/CustomPagination';
import PostCard from '@/components/blog/PostCard';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import AnalyticsTracker from '@/components/analytics/AnalyticsTracker';

interface UserBlogPageProps {
  params: Promise<{
    blogSlug: string;
  }>;
  searchParams: Promise<{
    page?: string;
    category?: string;
  }>;
}

export async function generateMetadata({ params }: UserBlogPageProps): Promise<Metadata> {
  const { blogSlug } = await params;

  const blogData = await getBlogLayoutAction(blogSlug);
  if (!blogData) {
    return {
      title: '블로그를 찾을 수 없습니다.',
      description: '요청하신 페이지를 찾을 수 없습니다.',
    };
  }

  return {
    title: `@${blogData.blog.slug} | Contentria`,
    description: `${blogData.owner.nickname}님의 블로그입니다. 다양한 글들을 만나보세요.`,
  };
}

export default async function BlogPage({ params, searchParams }: UserBlogPageProps) {
  const { blogSlug } = await params;
  const { page, category } = await searchParams;

  const currentPage = page ? Math.max(0, parseInt(page, 10) - 1) : 0;
  if (page && (isNaN(currentPage) || parseInt(page, 10) < 1)) {
    notFound();
  }

  const [layoutData, postsPage] = await Promise.all([
    getBlogLayoutAction(blogSlug),
    getBlogPostsAction(blogSlug, { page: currentPage, size: 5, categorySlug: category }),
  ]);

  if (!layoutData) {
    notFound();
  }

  if (postsPage && currentPage >= postsPage.page.totalPages && postsPage.page.totalPages > 0) {
    notFound();
  }

  // const { blog, owner, categories } = layoutData;
  const initialPosts = postsPage?.content ?? [];
  const totalPages = postsPage?.page.totalPages ?? 0;

  return (
    <>
      <AnalyticsTracker blogId={layoutData.blog.id} />

      <div className="mx-auto w-full max-w-4xl px-4 py-6">
        <h1 className="mb-8 text-3xl font-bold">최신 글</h1>
        <div className="space-y-4">
          {initialPosts.length > 0 ? (
            initialPosts.map((post) => <PostCard key={post.id} blogSlug={blogSlug} post={post} />)
          ) : (
            <div className="flex h-40 items-center justify-center rounded-lg border-2 border-dashed border-gray-300">
              <p className="text-center text-gray-500">아직 작성된 글이 없습니다.</p>
            </div>
          )}
        </div>

        <div className="mt-10">
          <CustomPagination totalPages={totalPages} />
        </div>
      </div>
    </>
  );
}
