export interface StatsResponse {
  todayVisitors: number;
  todayVisitorsGrowthRate: number | null;
  todayViews: number;
  todayViewsGrowthRate: number | null;
  totalViews: number;
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
