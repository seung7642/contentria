export interface User {
  id: string;
  email: string;
  username: string;
  nickname: string;
  profileImage: string | null;
  // blogs: BlogSummary[];
}

export interface UserSummaryResponse {
  username: string;
  pictureUrl: string | null;
}

export interface AuthorResponse {
  userId: string;
  username: string;
  profileImageUrl: string | null;
}

export interface OwnerResponse {
  userId: string;
  nickname: string;
  profileImageUrl: string | null;
}
