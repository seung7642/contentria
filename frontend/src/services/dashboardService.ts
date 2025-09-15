import apiClient from '@/lib/apiClient';

export interface Stats {
  todayVisitors: number;
  weekVisitors: number;
  weekNewComments: number;
  totalSubscribers: number;
}

export interface PopularPost {
  id: string;
  title: string;
  views: number;
}

export type TimeRange = '2weeks' | '30days' | '90days';

export interface ChartData {
  date: string;
  visitors: number;
}

export const dashboardService = {
  async getStats(slug: string): Promise<Stats> {
    const { data } = await apiClient.get<Stats>(`/api/blogs/${slug}/dashboard/stats`);
    return data;
  },

  async getPopularPosts(slug: string): Promise<PopularPost[]> {
    const { data } = await apiClient.get<PopularPost[]>(
      `/api/blogs/${slug}/dashboard/popular-posts`
    );
    return data;
  },

  async getTrafficData(slug: string, timeRange: TimeRange): Promise<ChartData[]> {
    const { data } = await apiClient.get<ChartData[]>(`/api/blogs/${slug}/dashboard/traffic`, {
      params: { timeRange },
    });
    return data;
  },
};
