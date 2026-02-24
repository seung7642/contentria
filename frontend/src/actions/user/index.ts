'use server';

import apiServer from '@/lib/apiServer';
import { User } from '@/types/api/user';
import { revalidatePath } from 'next/cache';
import { UpdateUserProfileRequest, updateUserProfileRequestSchema } from './schemas';

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

export async function updateUserProfileAction(formData: unknown): Promise<User> {
  const validationResult = updateUserProfileRequestSchema.safeParse(formData);
  if (!validationResult.success) {
    console.error('Profile update validation failed:', validationResult.error);
    throw new Error('Invalid profile data. Please check your input and try again.');
  }
  const validatedPayload: UpdateUserProfileRequest = validationResult.data;

  try {
    const response = await apiServer.put<User>('/api/users/me', validatedPayload, {
      requireAuth: true,
    });

    revalidatePath('/dashboard/settings');

    return response;
  } catch (error) {
    console.error('Update profile error:', error);
    throw error;
  }
}
