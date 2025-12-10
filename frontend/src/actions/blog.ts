'use server';

import apiServer from '@/lib/apiServer';
import { BlogLayout, CreateBlogPayload, CreateBlogResponse } from '@/types/api/blogs';

export async function getBlogLayoutAction(slug: string): Promise<BlogLayout | null> {
  return await apiServer.get<BlogLayout>(`/api/blogs/layout/${slug}`, {
    requireAuth: false,
    next: {
      revalidate: 3600, // 1시간 캐시
    },
  });
}

export async function createBlogAction(payload: CreateBlogPayload): Promise<CreateBlogResponse> {
  return await apiServer.post<CreateBlogResponse>('/api/blogs/create', payload, {
    requireAuth: true,
  });
}
