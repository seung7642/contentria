import { getBlogLayoutAction } from '@/actions/blog';
import { getBlogPostsAction } from '@/actions/post';
import BlogPagination from '@/components/blog/BlogPagination';
import PostCard from '@/components/blog/PostCard';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';

// Next.js 15 버전부터 params와 searchParams는 비동기 처리가 가능하다.
interface UserBlogPageProps {
  params: {
    blogSlug: string;
  };
  searchParams: {
    page?: string;
  };
}

// SEO를 위한 동적 메타데이터 생성 (매우 중요!)
export async function generateMetadata({ params }: UserBlogPageProps): Promise<Metadata> {
  const { blogSlug } = params;

  const blogData = await getBlogLayoutAction(blogSlug);
  if (!blogData) {
    return {
      title: '블로그를 찾을 수 없습니다.',
      description: '요청하신 페이지를 찾을 수 없습니다.',
    };
  }

  return {
    title: `@${blogData.blog.slug} | Contentria`,
    description: `${blogData.owner.username}님의 블로그입니다. 다양한 글들을 만나보세요.`,
  };
}

export default async function BlogPage({ params, searchParams }: UserBlogPageProps) {
  const { blogSlug } = params;
  const { page } = searchParams;

  // 페이지 번호 파싱 (1-based를 0-based로 변환)
  const currentPage = page ? Math.max(0, parseInt(page, 10) - 1) : 0;

  // 잘못된 페이지 번호 처리
  if (page && (isNaN(currentPage) || parseInt(page, 10) < 1)) {
    notFound();
  }

  const [layoutData, postsPage] = await Promise.all([
    getBlogLayoutAction(blogSlug),
    getBlogPostsAction(blogSlug),
  ]);

  if (!layoutData) {
    notFound();
  }

  if (postsPage && currentPage >= postsPage.totalPages && postsPage.totalPages > 0) {
    notFound();
  }

  // const { blog, owner, categories } = layoutData;
  const initialPosts = postsPage?.content ?? [];
  const totalPages = postsPage?.totalPages ?? 0;

  return (
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

      {totalPages > 0 && (
        <div className="mt-10">
          <BlogPagination currentPage={currentPage} totalPages={totalPages} blogSlug={blogSlug} />
        </div>
      )}
    </div>
  );
}
