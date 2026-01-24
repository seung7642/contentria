'use server';

import apiServer from '@/lib/apiServer';
import { UpdateProfilePayload, User } from '@/types/api/user';

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

export async function updateUserProfileAction(payload: UpdateProfilePayload): Promise<User> {
  return await apiServer.put<User>('/api/users/profile', payload, { requireAuth: true });
}
