export interface CategoryNode {
  id: string | null;
  name: string;
  slug: string;
  postCount: number;
  children: CategoryNode[];
}

export interface BlogInfo {
  id: string;
  slug: string;
  title: string;
  description: string | null;
}

export interface OwnerInfo {
  id: string;
  username: string;
  pictureUrl: string | null;
}

export interface BlogLayout {
  blog: BlogInfo;
  owner: OwnerInfo;
  categories: CategoryNode[];
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
