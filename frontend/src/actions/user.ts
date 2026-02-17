'use server';

import apiServer from '@/lib/apiServer';
import { ProfileFormValues } from '@/lib/schemas/userSchemas';
import { User } from '@/types/api/user';
import { revalidatePath } from 'next/cache';

export async function getUserProfileAction(
  shouldRedirectOn401: boolean = true
): Promise<User | null> {
  try {
    return await apiServer.get<User>('/api/users/me', { requireAuth: true, shouldRedirectOn401 });
  } catch (error: any) {
    if (error.status === 401 && !shouldRedirectOn401) {
      return null;
    }
    throw error;
  }
}

export async function updateUserProfileAction(data: ProfileFormValues) {
  try {
    await apiServer.put('/api/users/me', {
      body: JSON.stringify(data),
      requireAuth: true,
    });

    revalidatePath('/dashboard/settings');
  } catch (error) {
    console.error('Update profile error:', error);
    throw error;
  }
}
