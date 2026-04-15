'use client';

import {
  useDashboadStatsQuery,
  usePopularPostsQuery,
  useTrafficChartQuery,
} from '@/hooks/queries/useDashboardQueries';
import Link from 'next/link';
import { useState } from 'react';
import PopularPostList from './PopularPostList';
import { ArrowRight, Eye, FileText, Loader2, MessageSquare } from 'lucide-react';
import TrafficChart from './TrafficChart';
import StatCard from './StatCard';
import { TimeRange } from '@/types/api/dashboard';
import { User } from '@/types/api/user';
import { BlogInfo } from '@/types/api/blogs';
import { formatTrend } from '@/lib/utils';

interface DashboardContentProps {
  user: User | null;
  blogInfos: BlogInfo[] | null;
}

export default function DashboardContent({ user, blogInfos }: DashboardContentProps) {
  const [timeRange, setTimeRange] = useState<TimeRange>('2weeks');
  const slug = blogInfos?.[0]?.slug;

  const { data: stats } = useDashboadStatsQuery(slug!);
  const { data: popularPosts } = usePopularPostsQuery(slug!);
  const { data: trafficChart, isFetching: isTrafficFetching } = useTrafficChartQuery(
    slug!,
    timeRange
  );

  const todayTrend = formatTrend(stats?.todayVisitorsGrowthRate);
  const todayViewsTrend = formatTrend(stats?.todayViewsGrowthRate);

  return (
    <div className="space-y-6">
      {/* 환영 헤더 */}
      <div className="mb-6 flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">대시보드</h1>
          <p className="mt-1 text-sm text-gray-500">
            안녕하세요, {user?.nickname || '관리자'}님! 오늘의 블로그 현황입니다.
          </p>
        </div>
        <Link
          href="/dashboard/posts/new"
          className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-700"
        >
          새 글 작성
        </Link>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <>
          <StatCard
            icon={<FileText size={24} />}
            title="오늘 방문자"
            value={stats?.todayVisitors ?? 0}
            trend={todayTrend.text}
            trendUp={todayTrend.isUp}
          />
          <StatCard
            icon={<FileText size={24} />}
            title="오늘 조회수"
            value={stats?.todayViews ?? 0}
            trend={todayViewsTrend.text}
            trendUp={todayViewsTrend.isUp}
          />
          <StatCard
            icon={<Eye size={24} />}
            title="전체 조회수"
            value={stats?.totalViews ?? 0}
            trend=""
            trendUp={true}
          />
          <StatCard
            icon={<MessageSquare size={24} />}
            title="전체 게시글 수"
            value={stats?.totalPosts ?? 0}
            trend=""
            trendUp={true}
          />
        </>
      </div>

      {/* 콘텐츠 그리드 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 트래픽 차트 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">트래픽 현황</h2>
            <select
              className="rounded-md border border-gray-300 px-2 py-1 text-sm"
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value as TimeRange)}
            >
              <option value="2weeks">지난 2주</option>
              <option value="30days">지난 30일</option>
              <option value="90days">지난 90일</option>
            </select>
          </div>
          {isTrafficFetching ? (
            <div className="flex h-[250px] items-center justify-center">
              <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
            </div>
          ) : (
            <TrafficChart data={trafficChart || []} />
          )}
        </div>

        {/* 인기 글 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">인기 게시글</h2>
            <Link href="/dashboard/posts" className="text-sm text-indigo-600 hover:text-indigo-800">
              전체보기
            </Link>
          </div>
          <PopularPostList posts={popularPosts || []} />
          <Link
            href="/dashboard/posts"
            className="mt-4 flex items-center justify-center text-sm text-indigo-600 hover:text-indigo-800"
          >
            모든 게시글 보기 <ArrowRight size={16} className="ml-1" />
          </Link>
        </div>
      </div>
    </div>
  );
}
