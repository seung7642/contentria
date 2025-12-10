'use server';

import apiServer from '@/lib/apiServer';
import { UpdateProfilePayload, User } from '@/types/api/user';
import { cookies } from 'next/headers';

export async function getUserProfileAction(
  shouldRedirectOn401: boolean = true
): Promise<User | null> {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get('accessToken');

  if (!accessToken) {
    if (shouldRedirectOn401) {
      const { redirect } = await import('next/navigation');
      redirect('/login');
    }

    return null;
  }

  return await apiServer.get<User>('/api/users/me', { requireAuth: true, shouldRedirectOn401 });
}

export async function updateUserProfileAction(payload: UpdateProfilePayload): Promise<User> {
  return await apiServer.put<User>('/api/users/profile', payload, { requireAuth: true });
}
