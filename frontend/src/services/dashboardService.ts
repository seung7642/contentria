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
  async getStats(): Promise<Stats> {
    // const { data } = await apiClient.get<Stats>('/api/dashboard/stats');
    // return data;

    // 임시 모의(mock) 데이터 반환
    return new Promise((resolve) =>
      setTimeout(
        () =>
          resolve({
            todayVisitors: 124,
            weekVisitors: 3824,
            weekNewComments: 128,
            totalSubscribers: 256,
          }),
        500
      )
    );
  },

  async getPopularPosts(): Promise<PopularPost[]> {
    // const { data } = await apiClient.get<PopularPost[]>('/api/dashboard/popular-posts');
    // return data;

    // 임시 모의(mock) 데이터 반환
    return new Promise((resolve) =>
      setTimeout(
        () =>
          resolve([
            { id: '1', title: 'Next.js 14와 React Server Components', views: 152 },
            { id: '2', title: '효율적인 상태 관리 전략', views: 128 },
            { id: '3', title: '모바일 최적화를 위한 디자인 팁', views: 98 },
          ]),
        800
      )
    );
  },

  async getTrafficData(timeRange: TimeRange): Promise<ChartData[]> {
    // const { data } = await apiClient.get<ChartData[]>('/api/dashboard/traffic', {
    //   params: { timeRange },
    // });
    // return data;

    // 임시 모의(mock) 데이터 반환
    const dataMap = {
      '2weeks': Array(14)
        .fill(null)
        .map((_, i) => ({
          date: `09/${String(i + 1).padStart(2, '0')}`,
          visitors: Math.floor(Math.random() * 80) + 20,
        })),
      '30days': Array(30)
        .fill(null)
        .map((_, i) => ({
          date: `08/${String(i + 1).padStart(2, '0')}`,
          visitors: Math.floor(Math.random() * 100) + 20,
        })),
      '90days': Array(12)
        .fill(null)
        .map((_, i) => ({
          date: `2025/${String(i + 1).padStart(2, '0')}`,
          visitors: Math.floor(Math.random() * 200) + 50,
        })),
    };
    return new Promise((resolve) => setTimeout(() => resolve(dataMap[timeRange]), 1000));
  },
};
