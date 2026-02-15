'use server';

import apiServer from '@/lib/apiServer';
import { Page } from '@/types/api/common';
import {
  CreateNewPostRequest,
  CreateNewPostResponse,
  PostDetailResponse,
  PostStatus,
  PostSummary,
  UpdatePostRequest,
  UpdatePostResponse,
} from '@/types/api/posts';

interface GetBlogPostsOptions {
  categorySlug?: string | null;
  statuses?: PostStatus[];
  page?: number;
  size?: number;
}

export async function getBlogPostsAction(
  blogSlug: string,
  options: GetBlogPostsOptions = {}
): Promise<Page<PostSummary> | null> {
  const { categorySlug = null, statuses = [], page = 0, size = 10 } = options;

  const query = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
  });

  if (categorySlug) {
    query.append('category', categorySlug);
  }

  if (statuses && statuses.length > 0) {
    query.append('statuses', statuses.join(','));
  }

  return await apiServer.get<Page<PostSummary>>(
    `/api/blogs/${blogSlug}/posts?${query.toString()}`,
    {
      requireAuth: false,
      next: {
        tags: [`posts-${blogSlug}`, ...statuses.map((status) => `posts-${blogSlug}-${status}`)], // 태그 기반 재검증(On-demand Revalidation)을 위해 유지
        revalidate: 60,
      },
    }
  );
}

export async function getPostDetailAction(
  blogSlug: string,
  postSlug: string
): Promise<PostDetailResponse | null> {
  return await apiServer.get<PostDetailResponse>(`/api/blogs/${blogSlug}/posts/${postSlug}`, {
    requireAuth: false,
  });
}

export async function getPostDetailByIdAction(postId: string): Promise<PostDetailResponse | null> {
  return await apiServer.get<PostDetailResponse>(`/api/posts/${postId}`, {
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

export async function updatePostAction(payload: UpdatePostRequest): Promise<UpdatePostResponse> {
  return await apiServer.post<UpdatePostResponse>(`/api/posts/${payload.postId}`, payload, {
    requireAuth: true,
  });
}
