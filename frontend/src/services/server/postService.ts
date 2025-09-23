import apiServer from '@/lib/server/apiServer';
import { Page } from '@/types/api/common';
import { PostSummary } from '@/types/api/posts';

export async function getBlogPosts(
  slug: string,
  page: number = 0,
  size: number = 10
): Promise<Page<PostSummary> | null> {
  // URLSearchParams를 사용하면 쿼리 스트링을 안전하게 만들 수 있다.
  const query = new URLSearchParams({
    blogSlug: slug,
    page: page.toString(),
    size: size.toString(),
  });

  const path = `/api/posts?${query.toString()}`;
  const postsPage = await apiServer.get<Page<PostSummary>>(path, {
    next: {
      tags: [`posts-${slug}`],
      revalidate: 60,
    },
  });
  return postsPage;
}
