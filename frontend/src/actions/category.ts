import apiServer from '@/lib/apiServer';
import { getUserProfileAction } from './user';
import { CategoryResponse } from '@/types/api/category';

export async function getCategoriesAction(): Promise<CategoryResponse[]> {
  try {
    const user = await getUserProfileAction();
    const blogId = user?.blogs[0]?.id;

    return await apiServer.get<CategoryResponse[]>(`/api/categories?blogId=${blogId}`, {
      requireAuth: true,
    });
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    return [];
  }
}
