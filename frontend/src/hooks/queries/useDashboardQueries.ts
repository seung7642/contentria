import { dashboardService, TimeRange } from '@/services/dashboardService';
import { useQuery } from '@tanstack/react-query';

export const useDashboadStatsQuery = () => {
  return useQuery({
    queryKey: ['dashboard', 'stats'],
    queryFn: () => dashboardService.getStats(),
  });
};

export const usePopularPostsQuery = () => {
  return useQuery({
    queryKey: ['dashboard', 'popular-posts'],
    queryFn: () => dashboardService.getPopularPosts(),
  });
};

export const useTrafficChartQuery = (timeRange: TimeRange) => {
  return useQuery({
    queryKey: ['dashboard', 'traffic', timeRange],
    queryFn: () => dashboardService.getTrafficData(timeRange),
    placeholderData: (previousData) => previousData,
  });
};
