import { BlogSummary } from './blogs';

export interface User {
  id: string;
  name: string;
  email: string;
  profileImage: string | null;
  blogs: BlogSummary[];
}

export interface UpdateProfilePayload {
  name: string;
}

export interface UserSummaryResponse {
  username: string;
  pictureUrl: string | null;
}
