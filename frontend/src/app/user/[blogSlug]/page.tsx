import BlogPagination from '@/components/blog/BlogPagination';
import PostCard from '@/components/blog/PostCard';
import { getBlogLayout } from '@/services/server/blogService';
import { getBlogPosts } from '@/services/server/postService';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';

// Next.js 15 버전부터 params와 searchParams는 비동기 처리가 가능하다.
interface UserBlogPageProps {
  params: Promise<{
    blogSlug: string;
  }>;
  searchParams: Promise<{
    page?: string;
  }>;
}

// SEO를 위한 동적 메타데이터 생성 (매우 중요!)
export async function generateMetadata({ params }: UserBlogPageProps): Promise<Metadata> {
  const { blogSlug } = await params;

  // 메타데이터 생성을 위해서도 데이터를 한번 가져온다.
  // Next.js는 동일한 인자의 fetch 요청을 자동으로 중복 제거(deduplication)하므로
  // 페이지 컴포넌트에서 한번 더 호출해도 네트워크 요청은 한번만 발생한다.
  const blogData = await getBlogLayout(blogSlug);
  if (!blogData) {
    return {
      title: '블로그를 찾을 수 없습니다.',
      description: '요청하신 페이지를 찾을 수 없습니다.',
    };
  }

  return {
    // title: `${blogData.blog.title} by ${blogData.owner.username}`,
    title: 'Contentria',
    description: `${blogData.owner.username}님의 블로그입니다. 다양한 글들을 만나보세요.`,
  };
}

export default async function BlogPage({ params, searchParams }: UserBlogPageProps) {
  const { blogSlug } = await params;
  const { page } = await searchParams;

  // 페이지 번호 파싱 (1-based를 0-based로 변환)
  const currentPage = page ? Math.max(0, parseInt(page, 10) - 1) : 0;

  // 잘못된 페이지 번호 처리
  if (page && (isNaN(currentPage) || parseInt(page, 10) < 1)) {
    notFound();
  }

  console.log('BlogPage params:', params, 'currentPage:', currentPage);

  // 서비스 함수를 호출하여 데이터를 가져온다.
  // 이 과정에서 401 에러가 발생하면 apiServer가 자동으로 로그인 페이지로 리다이렉트한다.
  const [layoutData, postsPage] = await Promise.all([
    getBlogLayout(blogSlug),
    getBlogPosts(blogSlug),
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
    <div>
      <h1 className="mb-6 text-3xl font-bold">글 목록</h1>
      <div className="space-y-6">
        {initialPosts.map((post) => (
          <PostCard key={post.id} blogSlug={blogSlug} post={post} />
        ))}
        {initialPosts.length === 0 && <p className="text-gray-500">아직 작성된 글이 없습니다.</p>}
      </div>

      {totalPages > 0 && (
        <BlogPagination currentPage={currentPage} totalPages={totalPages} blogSlug={blogSlug} />
      )}
    </div>
  );
}
