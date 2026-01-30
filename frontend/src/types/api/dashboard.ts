export interface StatsResponse {
  todayViews: number;
  todayViewsGrowthRate: number | null;
  todayVisitors: number;
  todayGrowthRate: number | null;
  weekVisitors: number;
  weekGrowthRate: number | null;
  totalPosts: number;
}

export interface PopularPostResponse {
  id: string;
  title: string;
  views: number;
}

export type TimeRange = '2weeks' | '30days' | '90days';

export interface TrafficChartResponse {
  date: string;
  visitors: number;
}
