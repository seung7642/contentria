import { ApiError } from '@/types/api/errors';
import apiServer from '@/lib/apiServer';
import { Page } from '@/types/api/common';
import {
  CreateNewPostRequest,
  CreateNewPostResponse,
  PostDetailResponse,
  PostSummary,
} from '@/types/api/posts';

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

export async function getPostDetail(
  blogSlug: string,
  postSlug: string
): Promise<PostDetailResponse | null> {
  const path = `/api/blogs/${blogSlug}/posts/${postSlug}`;
  const response = await apiServer.get<PostDetailResponse>(path);
  return response;
}

export async function createNewPost(
  createNewPostRequest: CreateNewPostRequest
): Promise<CreateNewPostResponse | ApiError | null> {
  const path = '/api/posts';
  const response = await apiServer.post<CreateNewPostResponse>(
    path,
    JSON.stringify(createNewPostRequest)
  );
  return response;
}
