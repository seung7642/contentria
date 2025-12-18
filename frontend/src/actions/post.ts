'use server';

import apiServer from '@/lib/apiServer';
import { Page } from '@/types/api/common';
import {
  CreateNewPostRequest,
  CreateNewPostResponse,
  PostDetailResponse,
  PostSummary,
} from '@/types/api/posts';
import { getUserProfileAction } from './user';

export async function getBlogPostsAction(
  slug: string,
  page: number = 0,
  size: number = 10
): Promise<Page<PostSummary> | null> {
  const query = new URLSearchParams({
    blogSlug: slug,
    page: page.toString(),
    size: size.toString(),
  });

  return await apiServer.get<Page<PostSummary>>(`/api/posts?${query.toString()}`, {
    requireAuth: false,
    next: {
      tags: [`posts-${slug}`], // 태그 기반 재검증(On-demand Revalidation)을 위해 유지
      revalidate: 60,
    },
  });
}

export async function getPostDetailAction(
  blogSlug: string,
  postSlug: string
): Promise<PostDetailResponse | null> {
  return await apiServer.get<PostDetailResponse>(`/api/blogs/${blogSlug}/posts/${postSlug}`, {
    requireAuth: false,
  });
}

export async function createNewPostAction(
  payload: CreateNewPostRequest
): Promise<CreateNewPostResponse> {
  return await apiServer.post<CreateNewPostResponse>('/api/posts', payload, {
    requireAuth: true,
  });
}
