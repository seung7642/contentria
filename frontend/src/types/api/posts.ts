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
  summary: string;
  contentHtml: string;
  metaTitle: string | null;
  metaDescription: string | null;
  featuredImageUrl: string | null;
  publishedAt: string;
  categoryName: string;
}

export interface OwnerInfo {
  username: string;
  pictureUrl: string | null;
}

export interface PostDetailResponse {
  post: PostDetail;
  owner: OwnerInfo;
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
