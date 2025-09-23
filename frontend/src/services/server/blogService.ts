import apiServer from '@/lib/server/apiServer';
import { BlogLayout } from '@/types/api/blogs';

export async function getBlogLayout(slug: string): Promise<BlogLayout | null> {
  const path = `/api/blogs/layout/${slug}`;
  const blogData = await apiServer.get<BlogLayout>(path, {
    next: {
      revalidate: 3600, // 1시간 캐시
    },
  });
  return blogData;
}
