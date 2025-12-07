'use server';

import apiServer01 from '@/lib/apiServer01';
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

  return await apiServer01.get<User>('/api/users/me', { requireAuth: true, shouldRedirectOn401 });
}

export async function updateUserProfileAction(payload: UpdateProfilePayload): Promise<User> {
  return await apiServer01.put<User>('/api/users/profile', payload, { requireAuth: true });
}
