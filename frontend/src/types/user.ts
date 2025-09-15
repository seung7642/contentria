export interface User {
  id: string;
  name: string;
  email: string;
  profileImage: string | null;
  slugs: string[] | null;
}
