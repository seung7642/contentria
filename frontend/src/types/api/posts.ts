import { AuthorResponse } from './user';
import { VideoStatus } from './video';

export interface PostSummary {
  id: string;
  slug: string;
  title: string;
  summary: string;
  metaTitle: string | null;
  metaDescription: string | null;
  status: PostStatus;
  featuredImageUrl: string | null;
  publishedAt: string;
  createdAt: string;
  updatedAt: string;
  likeCount: number;
  viewCount: number;
  categoryId: string | null;
  categoryName: string | null;
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

export interface PostVideo {
  videoId: string;
  status: VideoStatus;
  masterUrl: string | null;
  posterUrl: string | null;
  durationMs: number | null;
  width: number | null;
  height: number | null;
}

export interface PostDetailResponse {
  post: PostDetail;
  author: AuthorResponse;
  blogId: string;
  blogSlug: string;
  categoryId: string | null;
  categoryName: string | null;
  video: PostVideo | null;
}

export type PostStatus = 'DRAFT' | 'PUBLISHED';

export interface CreateNewPostRequest {
  blogId: string;
  title: string;
  contentMarkdown: string;
  status: PostStatus;
  categoryId: string;
  videoId?: string | null;
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

export interface UpdatePostRequest {
  postId: string;
  blogId: string;
  title: string;
  contentMarkdown: string;
  status: PostStatus;
  categoryId: string;
  videoId?: string | null;
}

export interface UpdatePostResponse {
  postId: string;
  slug: string;
  title: string;
  metaTitle: string | null;
  metaDescription: string | null;
  publishedAt: string | null;
  status: PostStatus;
  categoryName: string | null;
}

/** 홈 "최근 발행된 글" 항목 (GET /posts/recent) */
export interface RecentPostResponse {
  title: string;
  summary: string;
  postSlug: string;
  blogSlug: string;
  authorNickname: string;
  publishedAt: string | null;
}
