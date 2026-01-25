'use server';

import apiServer from '@/lib/apiServer';
import { TrackVisitPayload } from '@/types/api/analytics';
import { headers } from 'next/headers';

export async function trackVisitAction(payload: TrackVisitPayload): Promise<void> {
  try {
    const headersList = await headers();
    const userAgent = headersList.get('user-agent') || '';
    const xForwardedFor = headersList.get('x-forwarded-for') || '';
    const realIp = headersList.get('x-real-ip') || '';

    await apiServer.post<void>('/api/analytics/visit', payload, {
      requireAuth: false,
      headers: {
        'User-Agent': userAgent,
        'X-Forwarded-For': xForwardedFor || realIp,
      },
    });
  } catch (error) {
    console.error('Failed to track visit:', error);
  }
}
