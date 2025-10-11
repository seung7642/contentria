import { getPostDetail } from '@/services/server/postService';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';

interface PostDetailPageProps {
  params: Promise<{
    blogSlug: string;
    postSlug: string;
  }>;
}

export async function generateMetadata({ params }: PostDetailPageProps): Promise<Metadata> {
  const { blogSlug, postSlug } = await params;
  const postData = await getPostDetail(blogSlug, postSlug);

  if (!postData) {
    return { title: '게시물을 찾을 수 없습니다.' };
  }

  return {
    title: postData.post.title,
    description: postData.post.metaDescription || '',
  };
}

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { blogSlug, postSlug } = await params;
  const postDetailResponse = await getPostDetail(blogSlug, postSlug);

  if (!postDetailResponse) {
    console.log('Post not found, invoking notFound()');
    notFound();
  }

  const { post } = postDetailResponse;

  return (
    <article>
      <h1 className="mb-4 text-4xl font-bold">{post.title}</h1>

      {/* 작성자 정보 및 발행일 */}
      <div className="mb-8 flex items-center space-x-4 text-gray-500">
        {/* <span>{author.username}</span> */}
        {/* <span>•</span> */}
        <span>{new Date(post.publishedAt).toLocaleDateString()}</span>
      </div>

      {/* 본문 내용 (HTML 렌더링) */}
      <div
        className="prose max-w-none" // tailwindcss-typography 플러그인 필요
        dangerouslySetInnerHTML={{ __html: post.contentHtml }}
      />
    </article>
  );
}
