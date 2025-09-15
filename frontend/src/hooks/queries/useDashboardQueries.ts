import { ApiError } from '@/errors/ApiError';
import {
  ChartData,
  dashboardService,
  PopularPost,
  Stats,
  TimeRange,
} from '@/services/dashboardService';
import { useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';

export const useDashboadStatsQuery = (slug: string) => {
  return useQuery<Stats, ApiError | AxiosError>({
    queryKey: ['dashboard', 'stats'],
    queryFn: () => dashboardService.getStats(slug),
  });
};

export const usePopularPostsQuery = (slug: string) => {
  return useQuery<PopularPost[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'popular-posts'],
    queryFn: () => dashboardService.getPopularPosts(slug),
  });
};

export const useTrafficChartQuery = (slug: string, timeRange: TimeRange) => {
  return useQuery<ChartData[], ApiError | AxiosError>({
    queryKey: ['dashboard', 'traffic', timeRange],
    queryFn: () => dashboardService.getTrafficData(slug, timeRange),
    placeholderData: (previousData) => previousData,
  });
};
