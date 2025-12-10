'use server';

import apiServer from '@/lib/apiServer';
import { ChartData, PopularPost, Stats, TimeRange } from '@/types/api/dashboard';

export async function getDashboardStatsAction(blogSlug: string): Promise<Stats> {
  return await apiServer.get<Stats>(`/api/blogs/${blogSlug}/dashboard/stats`, {
    requireAuth: true,
  });
}

export async function getPopularPostsAction(blogSlug: string): Promise<PopularPost[]> {
  return await apiServer.get<PopularPost[]>(`/api/blogs/${blogSlug}/dashboard/popular-posts`, {
    requireAuth: true,
  });
}

export async function getTrafficDataAction(
  blogSlug: string,
  timeRange: TimeRange
): Promise<ChartData[]> {
  const query = new URLSearchParams({ timeRange });

  return await apiServer.get<ChartData[]>(
    `/api/blogs/${blogSlug}/dashboard/traffic?${query.toString()}`,
    {
      requireAuth: true,
    }
  );
}
