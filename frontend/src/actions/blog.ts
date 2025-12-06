'use server';

import apiServer01 from '@/lib/apiServer01';
import { BlogLayout, CreateBlogPayload, CreateBlogResponse } from '@/types/api/blogs';

export async function getBlogLayoutAction(slug: string): Promise<BlogLayout | null> {
  return await apiServer01.get<BlogLayout>(`/api/blogs/layout/${slug}`, {
    requireAuth: false,
    next: {
      revalidate: 3600, // 1시간 캐시
    },
  });
}

export async function createBlogAction(payload: CreateBlogPayload): Promise<CreateBlogResponse> {
  return await apiServer01.post<CreateBlogResponse>('/api/blogs/create', payload, {
    requireAuth: true,
  });
}
