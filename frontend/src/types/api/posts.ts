import { AuthorResponse, UserSummaryResponse } from './user';

export interface PostSummary {
  id: string;
  slug: string;
  title: string;
  summary: string;
  metaTitle: string | null;
  metaDescription: string | null;
  featuredImageUrl: string | null;
  publishedAt: string;
  categoryName: string | null;
  likeCount: number;
  viewCount: number;
}

export interface PostDetail {
  id: string;
  slug: string;
  title: string;
  contentMarkdown: string;
  metaTitle: string | null;
  metaDescription: string | null;
  featuredImageUrl: string | null;
  publishedAt: string;
}

export interface PostDetailResponse {
  post: PostDetail;
  author: AuthorResponse;
  blogSlug: string;
  categoryName: string | null;
}

export type PostStatus = 'DRAFT' | 'PUBLISHED';

export interface CreateNewPostRequest {
  blogId: string;
  title: string;
  contentMarkdown: string;
  status: PostStatus;
  categoryId: string;
}

export interface CreateNewPostResponse {
  postId: string;
  slug: string;
  title: string;
  metaTitle: string | null;
  metaDescription: string | null;
  publishedAt: string | null;
  status: PostStatus;
  categoryName: string | null;
}
