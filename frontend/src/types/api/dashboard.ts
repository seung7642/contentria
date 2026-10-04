export interface StatsResponse {
  todayVisitors: number;
  yesterdayVisitors: number;
  totalVisitors: number;
  todayViews: number;
  yesterdayViews: number;
  totalViews: number;
  totalPosts: number;
}

export interface PopularPostResponse {
  id: string;
  title: string;
  /** 리더 URL(/@{blogSlug}/{slug}) 구성용 게시글 슬러그 */
  slug: string;
  views: number;
}

export type TimeRange = '2weeks' | '30days' | '90days';

export interface TrafficChartResponse {
  /** 30days 범위에서는 ISO(yyyy-MM-dd) — 차트가 일/월 축 라벨을 직접 구성한다 */
  date: string;
  visitors: number;
  views: number;
}
