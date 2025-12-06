'use client';

import { ApiError } from '@/types/api/errors';
import { useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import {
  getDashboardStatsAction,
  getPopularPostsAction,
  getTrafficDataAction,
} from '@/actions/dashboard';
import { ChartData, PopularPost, Stats, TimeRange } from '@/types/api/dashboard';

export const useDashboadStatsQuery = (slug: string) => {
  return useQuery<Stats, ApiError | AxiosError>({
    queryKey: ['dashboard', 'stats', slug],
    queryFn: () => getDashboardStatsAction(slug),
  });
};

export const usePopularPostsQuery = (slug: string) => {
  return useQuery<PopularPost[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'popular-posts', slug],
    queryFn: () => getPopularPostsAction(slug),
  });
};

export const useTrafficChartQuery = (slug: string, timeRange: TimeRange) => {
  return useQuery<ChartData[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'traffic', slug, timeRange],
    queryFn: () => getTrafficDataAction(slug, timeRange),
    placeholderData: (previousData) => previousData,
  });
};
