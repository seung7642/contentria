'use server';

import apiServer from '@/lib/apiServer';
import { CategoryResponse } from '@/types/api/category';
import { getMyBlogAction } from './blog';

export async function getCategoriesAction(): Promise<CategoryResponse[]> {
  try {
    const blog = await getMyBlogAction();
    const blogId = blog?.[0]?.id;
    if (!blogId) {
      return [];
    }

    return await apiServer.get<CategoryResponse[]>(`/api/categories?blogId=${blogId}`, {
      requireAuth: true,
    });
  } catch (error) {
    console.error('Failed to fetch categories:', error);
    return [];
  }
}
