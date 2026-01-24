'use server';

import apiServer from '@/lib/apiServer';
import {
  BlogInfo,
  BlogLayoutResponse,
  CreateBlogPayload,
  CreateBlogResponse,
} from '@/types/api/blogs';

export async function getMyBlogAction(): Promise<BlogInfo[] | null> {
  return await apiServer.get<BlogInfo[]>(`/api/blogs/me`, {
    requireAuth: true,
  });
}

export async function getBlogLayoutAction(slug: string): Promise<BlogLayoutResponse | null> {
  return await apiServer.get<BlogLayoutResponse>(`/api/blogs/layout/${slug}`, {
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
