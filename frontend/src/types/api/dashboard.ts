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
