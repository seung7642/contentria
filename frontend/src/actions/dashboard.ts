'use server';

import apiServer01 from '@/lib/apiServer01';
import { ChartData, PopularPost, Stats, TimeRange } from '@/types/api/dashboard';

export async function getDashboardStatsAction(blogSlug: string): Promise<Stats> {
  return await apiServer01.get<Stats>(`/api/blogs/${blogSlug}/dashboard/stats`, {
    requireAuth: true,
  });
}

export async function getPopularPostsAction(blogSlug: string): Promise<PopularPost[]> {
  return await apiServer01.get<PopularPost[]>(`/api/blogs/${blogSlug}/dashboard/popular-posts`, {
    requireAuth: true,
  });
}

export async function getTrafficDataAction(
  blogSlug: string,
  timeRange: TimeRange
): Promise<ChartData[]> {
  const query = new URLSearchParams({ timeRange });

  return await apiServer01.get<ChartData[]>(
    `/api/blogs/${blogSlug}/dashboard/traffic?${query.toString()}`,
    {
      requireAuth: true,
    }
  );
}
