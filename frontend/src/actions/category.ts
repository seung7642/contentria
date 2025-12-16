import apiServer from '@/lib/apiServer';
import { getUserProfileAction } from './user';

export async function getCategoriesAction(): Promise<Category[]> {
  try {
    const user = await getUserProfileAction();
    const blogId = user?.blogs[0]?.id;

    return await apiServer.get<Category[]>(`/api/categories/dropdown?blogId=${blogId}`, {
      requireAuth: true,
    });
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    return [];
  }
}
