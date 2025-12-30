import { UserSummaryResponse } from './user';
import { CategoryResponse } from './category';

export interface CategoryNode {
  id: string | null;
  name: string;
  slug: string;
  parentId: string | null;
  level: number;
  postCount: number;
  // children: CategoryNode[];
}

export interface BlogSummary {
  id: string;
  slug: string;
  title: string;
  description: string | null;
}

export interface BlogLayoutResponse {
  blog: BlogSummary;
  owner: UserSummaryResponse;
  categories: CategoryResponse[];
}

export interface CreateBlogPayload {
  slug: string;
}

export interface CreateBlogResponse {
  id: string;
  slug: string;
  title: string;
  description: string | null;
  createdAt: string;
}
