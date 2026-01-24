import { BlogInfo } from './blogs';

export interface User {
  id: string;
  name: string;
  email: string;
  profileImage: string | null;
  // blogs: BlogSummary[];
}

export interface UpdateProfilePayload {
  name: string;
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
  username: string;
  profileImageUrl: string | null;
}
