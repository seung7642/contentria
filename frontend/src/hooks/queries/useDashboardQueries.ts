'use client';

import { ApiError } from '@/types/api/errors';
import { useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import {
  getDashboardStatsAction,
  getPopularPostsAction,
  getTrafficDataAction,
} from '@/actions/dashboard';
import {
  TrafficChartResponse,
  PopularPostResponse,
  StatsResponse,
  TimeRange,
} from '@/types/api/dashboard';

export const useDashboadStatsQuery = (slug: string) => {
  return useQuery<StatsResponse, ApiError | AxiosError>({
    queryKey: ['dashboard', 'stats', slug],
    queryFn: () => getDashboardStatsAction(slug),
  });
};

export const usePopularPostsQuery = (slug: string) => {
  return useQuery<PopularPostResponse[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'popular-posts', slug],
    queryFn: () => getPopularPostsAction(slug),
  });
};

export const useTrafficChartQuery = (slug: string, timeRange: TimeRange) => {
  return useQuery<TrafficChartResponse[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'traffic', slug, timeRange],
    queryFn: () => getTrafficDataAction(slug, timeRange),
    staleTime: 5 * 60 * 1000, // 5 minutes
    placeholderData: (previousData) => previousData,
  });
};
