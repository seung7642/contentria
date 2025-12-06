'use server';

import apiServer01 from '@/lib/apiServer01';
import { UpdateProfilePayload } from '@/services/userService';
import { User } from '@/types/api/user';

export async function getUserProfileAction(): Promise<User> {
  return await apiServer01.get<User>('/api/users/me', { requireAuth: true });
}

export async function updateUserProfileAction(payload: UpdateProfilePayload): Promise<User> {
  return await apiServer01.put<User>('/api/users/profile', payload, { requireAuth: true });
}

export async function serverActionTest(): Promise<string> {
  console.log('Server Action Test Invoked');
  return 'Server Action Test Successful';
}
